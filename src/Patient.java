public class Patient {
    private String patientId, doctorId;
    private int queueNum;

    public static Comparator<Patient> queueNumComp = new Comparator<Patient>() {
        @Override
        public long compare(Patient o1, Patient o2) {
            return (long)o1.queueNum - o2.queueNum;
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

    public static Comparator<Patient> comparator = new Comparator<Patient>() {
        @Override
        public long compare(Patient o1, Patient o2) {
            return o1.patientId.compareTo(o2.patientId);
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

    public String toString() {
        return getPatientId() + " at doctor " + getDoctorId() + " with queue number " + getQueueNum();
    }

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
