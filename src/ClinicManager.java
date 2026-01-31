public class ClinicManager {
    public static final String MIN_ID = "";
    public static final String MAX_ID = "\uFFFF\uFFFF\uFFFF\uFFFF";

    private final TwoThreeTree<Doctor> doctorsById;
    private final TwoThreeTree<Patient> patientsById;
    private final TwoThreeTree<Doctor> doctorsByLoad;

    public ClinicManager() {
        this.doctorsById = new TwoThreeTree<>(Doctor.byId, null);
        this.patientsById = new TwoThreeTree<>(Patient.byId, null);
        this.doctorsByLoad = new TwoThreeTree<>(Doctor.byPatientCount, doctor -> doctor.getPatientTree().getSize());
    }

    public void doctorEnter(String doctorId) {
        Doctor newDoctor = new Doctor(doctorId);
        if (doctorsById.search(newDoctor) != null) {
            throw new IllegalArgumentException();
        }
        doctorsById.insert(newDoctor);
        doctorsByLoad.insert(newDoctor);
    }

    public void doctorLeave(String doctorId) {
        Doctor doctorToRemove = doctorsById.search(new Doctor(doctorId));
        // If doctor was not found or has patients, throw an exception
        if (doctorToRemove == null || !doctorToRemove.getPatientTree().isEmpty()) {
            throw new IllegalArgumentException();
        }
        doctorsById.delete(doctorToRemove);
        doctorsByLoad.delete(doctorToRemove);
    }

    public void patientEnter(String doctorId, String patientId) {
        Doctor doctor = doctorsById.search(new Doctor(doctorId));
        Patient newPatient = new Patient(patientId, doctorId);

        if (doctor == null || patientsById.search(newPatient) != null) {
            throw new IllegalArgumentException();
        }

        // delete doctor before it is changed
        doctorsByLoad.delete(doctor);

        newPatient.setQueueNum(doctor.newQueueNum());
        patientsById.insert(newPatient);
        doctor.getPatientTree().insert(newPatient);

        // reinsert afterwards to update load tree
        doctorsByLoad.insert(doctor);
    }

    public String nextPatientLeave(String doctorId) {
        Doctor doctor = doctorsById.search(new Doctor(doctorId));

        if (doctor == null || doctor.getPatientTree().isEmpty()) {
            throw new IllegalArgumentException();
        }

        // delete doctor before it is changed
        doctorsByLoad.delete(doctor);

        Patient nextPatient = doctor.getPatientTree().popMin();
        patientsById.delete(nextPatient);

        // reinsert afterwards to update load tree
        doctorsByLoad.insert(doctor);

        return nextPatient.getPatientId();
    }

    public void patientLeaveEarly(String patientId) {
        Patient patient = patientsById.search(new Patient(patientId, ""));

        if (patient == null) {
            throw new IllegalArgumentException();
        }

        Doctor doctor = doctorsById.search(new Doctor(patient.getDoctorId()));

        // delete doctor before it is changed
        doctorsByLoad.delete(doctor);

        patientsById.delete(patient);
        // Find patient by their QUEUE NUMBER and delete them.
        doctor.getPatientTree().delete(patient);

        // reinsert afterwards to update load tree
        doctorsByLoad.insert(doctor);
    }

    public int numPatients(String doctorId) {
        Doctor doctor = doctorsById.search(new Doctor(doctorId));
        if (doctor == null) {
            throw new IllegalArgumentException();
        }
        return doctor.getPatientTree().getSize();
    }

    public String nextPatient(String doctorId) {
        Doctor doctor = doctorsById.search(new Doctor(doctorId));
        if (doctor == null || doctor.getPatientTree().isEmpty()) {
            throw new IllegalArgumentException();
        }
        return doctor.getPatientTree().getMin().getPatientId();
    }

    public String waitingForDoctor(String patientId) {
        Patient patient = patientsById.search(new Patient(patientId, ""));
        if (patient == null) {
            throw new IllegalArgumentException();
        }
        return patient.getDoctorId();
    }

    public int numDoctorsWithLoadInRange(int low, int high) {
        if (high < low) {
            return 0;
        }

        if (low < 0) {
            low = 0;
        }

        Doctor lowerBound = Doctor.createDummyWithVirtualSize(low - 1);
        Doctor upperBound = Doctor.createDummyWithVirtualSize(high);

        int lowerNum = doctorsByLoad.aggregateLower(lowerBound, true, false);
        int upperNum = doctorsByLoad.aggregateLower(upperBound, true, false);
        return upperNum - lowerNum;
    }

    public int averageLoadWithinRange(int low, int high) {
        if (high < low) {
            return 0;
        }

        if (low < 0) {
            low = 0;
        }

        Doctor lowerBound = Doctor.createDummyWithVirtualSize(low - 1);
        Doctor upperBound = Doctor.createDummyWithVirtualSize(high);

        int lowerNum = doctorsByLoad.aggregateLower(lowerBound, true, false);
        int upperNum = doctorsByLoad.aggregateLower(upperBound, true, false);

        int lowerSum = doctorsByLoad.aggregateLower(lowerBound, true, true);
        int upperSum = doctorsByLoad.aggregateLower(upperBound, true, true);

        if (lowerNum == upperNum) {
            return 0;
        }

        return (upperSum - lowerSum) / (upperNum - lowerNum);
    }
}