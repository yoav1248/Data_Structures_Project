public class Doctor {
    private static final int UNSET_SIZE = -2;

    private final String doctorId;
    private final TwoThreeTree<Patient> patientTree;
    private int queueNum;
    private int virtualSize = UNSET_SIZE;

    public Doctor(String doctorId) {
        this.doctorId = doctorId;
        this.patientTree = new TwoThreeTree<>(Patient.byQueueNumber, null);
        this.queueNum = 0;
    }

    public TwoThreeTree<Patient> getPatientTree() {
        return patientTree;
    }

    public int newQueueNum() {
        return queueNum++;
    }

    // Creates a dummy doctor object used for searching by size
    public static Doctor createDummyWithVirtualSize(int size) {
        Doctor doc = new Doctor(ClinicManager.MAX_ID);
        doc.virtualSize = size;
        return doc;
    }

    public static Comparator<Doctor> byId = new Comparator<>() {
        @Override
        public long compare(Doctor d1, Doctor d2) {
            return d1.doctorId.compareTo(d2.doctorId);
        }

        @Override
        public Doctor MAX() {
            return new Doctor(ClinicManager.MAX_ID);
        }

        @Override
        public Doctor MIN() {
            return new Doctor(ClinicManager.MIN_ID);
        }
    };

    private int getEffectiveSize() {
        if (virtualSize == UNSET_SIZE) {
            return patientTree.getSize();
        }
        return virtualSize;
    }

    public static Comparator<Doctor> byPatientCount = new Comparator<>() {
        @Override
        public long compare(Doctor d1, Doctor d2) {
            long sizeDiff = (long) d1.getEffectiveSize() - d2.getEffectiveSize();
            if (sizeDiff != 0)
                return sizeDiff;

            return d1.doctorId.compareTo(d2.doctorId);
        }

        @Override
        public Doctor MAX() {
            return createDummyWithVirtualSize(Integer.MAX_VALUE);
        }

        @Override
        public Doctor MIN() {
            return createDummyWithVirtualSize(Integer.MIN_VALUE);
        }
    };
}