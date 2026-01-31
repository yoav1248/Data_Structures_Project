public class Patient {
    private String patientId;
    private String doctorId;
    private int queueNum;

    // comparator for queue number (smaller number = higher priority)
    public static Comparator<Patient> byQueueNumber = new Comparator<>() {
        @Override
        public long compare(Patient p1, Patient p2) {
            return (long) p1.queueNum - p2.queueNum;
        }

        @Override
        public Patient MAX() {
            return new Patient("", "", Integer.MAX_VALUE);
        }

        @Override
        public Patient MIN() {
            return new Patient("", "", Integer.MIN_VALUE);
        }
    };

    public static Comparator<Patient> byId = new Comparator<Patient>() {
        @Override
        public long compare(Patient p1, Patient p2) {
            return p1.patientId.compareTo(p2.patientId);
        }

        @Override
        public Patient MAX() {
            return new Patient(ClinicManager.MAX_ID, "", 0);
        }

        @Override
        public Patient MIN() {
            return new Patient(ClinicManager.MIN_ID, "", 0);
        }
    };

    public String getPatientId() {
        return patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public int getQueueNum() {
        return queueNum;
    }

    public void setQueueNum(int num) {
        this.queueNum = num;
    }

    public Patient(String patientId, String doctorId) {
        this(patientId, doctorId, 0);
    }

    private Patient(String patientId, String doctorId, int queueNum) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.queueNum = queueNum;
    }
}