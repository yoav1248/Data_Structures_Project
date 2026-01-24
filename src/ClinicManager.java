public class ClinicManager {
    public static final String MIN_ID = "";
    public static final String MAX_ID = "\uFFFF\uFFFF\uFFFF\uFFFF";

    TwoThreeTree<Doctor> doctorTree;
    TwoThreeTree<Patient> allPatientTree;
    TwoThreeTree<Doctor> numPatientsTree;

    public ClinicManager() {
        doctorTree = new TwoThreeTree<>(Doctor.comparator, null);
        allPatientTree = new TwoThreeTree<>(Patient.comparator, null);
        numPatientsTree = new TwoThreeTree<>(Doctor.treeSizeComp, (Doctor d) -> d.getPatientTree().getSize());
    }

    public void doctorEnter(String doctorId) {
        Doctor doc = new Doctor(doctorId);
        if (doctorTree.search(doc) != null) {
            throw new IllegalArgumentException();
        }
        doctorTree.insert(doc);
        numPatientsTree.insert(doc);
    }

    public void doctorLeave(String doctorId) {
        Doctor doc = doctorTree.search(new Doctor(doctorId));
        // If doctor was not found or has patients, throw an exception
        if (doc == null || !doc.getPatientTree().isEmpty()) {
            throw new IllegalArgumentException();
        }
        doctorTree.delete(doc);
        numPatientsTree.delete(doc);
    }

    public void patientEnter(String doctorId, String patientId) {
        Doctor doc = doctorTree.search(new Doctor(doctorId));
        Patient patient = new Patient(patientId, doctorId);

        if (doc == null || allPatientTree.search(patient) != null) {
            throw new IllegalArgumentException();
        }

        // delete doc before it is changed
        numPatientsTree.delete(doc);

        patient.setQueueNum(doc.newQueueNum());
        allPatientTree.insert(patient);
        doc.getPatientTree().insert(patient);

        // reinsert afterwards to update patient count tree
        numPatientsTree.insert(doc);
    }

    public String nextPatientLeave(String doctorId) {
        Doctor doc = doctorTree.search(new Doctor(doctorId));

        if (doc == null || doc.getPatientTree().isEmpty()) {
            throw new IllegalArgumentException();
        }

        // delete doc before it is changed
        numPatientsTree.delete(doc);

        Patient nextPatient = doc.getPatientTree().popMin();
        allPatientTree.delete(nextPatient);

        // reinsert afterwards to update patient count tree
        numPatientsTree.insert(doc);

        return nextPatient.getPatientId();
    }

    public void patientLeaveEarly(String patientId) {
        Patient patient = allPatientTree.search(new Patient(patientId, ""));

        if (patient == null) {
            throw new IllegalArgumentException();
        }

        Doctor doc = doctorTree.search(new Doctor(patient.getDoctorId()));

        // delete doc before it is changed
        numPatientsTree.delete(doc);

        allPatientTree.delete(patient);
        // Find patient by their QUEUE NUMBER and delete them.
        doc.getPatientTree().delete(patient);


        // reinsert afterwards to update patient count tree
        numPatientsTree.insert(doc);
    }

    public int numPatients(String doctorId) {
        Doctor doc = doctorTree.search(new Doctor(doctorId));
        if (doc == null) {
            throw new IllegalArgumentException();
        }
        return doc.getPatientTree().getSize();
    }

    public String nextPatient(String doctorId) {
        Doctor doc = doctorTree.search(new Doctor(doctorId));
        if (doc == null || doc.getPatientTree().isEmpty()) {
            throw new IllegalArgumentException();
        }
        return doc.getPatientTree().getMin().getPatientId();
    }

    public String waitingForDoctor(String patientId) {
        Patient patient = allPatientTree.search(new Patient(patientId, ""));
        if (patient == null) {
            throw new IllegalArgumentException();
        }
        return patient.getDoctorId();
    }

    public int numDoctorsWithLoadInRange(int low, int high) {
        Doctor lowerDoctor = Doctor.buildFakeTreeSizeDoctor(low - 1);
        Doctor upperDoctor = Doctor.buildFakeTreeSizeDoctor(high);

        int lowerNum = numPatientsTree.aggregateLower(lowerDoctor, true, false);
        int upperNum = numPatientsTree.aggregateLower(upperDoctor, true, false);
        return upperNum - lowerNum;
    }

    public int averageLoadWithinRange(int low, int high) {
        Doctor lowerDoctor = Doctor.buildFakeTreeSizeDoctor(low - 1);
        Doctor upperDoctor = Doctor.buildFakeTreeSizeDoctor(high);

        int lowerNum = numPatientsTree.aggregateLower(lowerDoctor, true, false);
        int upperNum = numPatientsTree.aggregateLower(upperDoctor, true, false);

        int lowerSum = numPatientsTree.aggregateLower(lowerDoctor, true, true);
        int upperSum = numPatientsTree.aggregateLower(upperDoctor, true, true);

        if (lowerNum == upperNum) {
            return 0;
        }

        return (upperSum - lowerSum) / (upperNum - lowerNum);
    }
}