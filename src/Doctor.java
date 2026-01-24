public class Doctor {
    private final String doctorId;
    private final TwoThreeTree<Patient> patientTree;
    private int queueNum;
    private int fakeTreeSize = -2;

    public Doctor(String doctorId) {
        this.doctorId = doctorId;
        this.patientTree = new TwoThreeTree<>(Patient.queueNumComp, null);
        this.queueNum = 0;
    }

    public TwoThreeTree<Patient> getPatientTree() {
        return patientTree;
    }

    public int newQueueNum() {
        return queueNum++;
    }

    public String toString() {
        return doctorId;
    }

    public static Doctor buildFakeTreeSizeDoctor(int val) {
        Doctor doc = new Doctor(ClinicManager.MAX_ID);
        doc.fakeTreeSize = val;
        return doc;
    }

    public static Comparator<Doctor> comparator = new Comparator<>() {
        @Override
        public long compare(Doctor o1, Doctor o2) {
            return o1.doctorId.compareTo(o2.doctorId);
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

    private int getFakeSize() {
        if (fakeTreeSize == -2) {
            return patientTree.getSize();
        }
        return fakeTreeSize;
    }

    public static Comparator<Doctor> treeSizeComp = new Comparator<>() {
        @Override
        public long compare(Doctor o1, Doctor o2) {
            long sizeDiff = (long) o1.getFakeSize() - o2.getFakeSize();
            if (sizeDiff != 0)
                return sizeDiff;
            // doctorId tie-breaker
            return o1.doctorId.compareTo(o2.doctorId);
        }

        @Override
        public Doctor MAX() {
            return buildFakeTreeSizeDoctor(Integer.MAX_VALUE);
        }

        @Override
        public Doctor MIN() {
            return buildFakeTreeSizeDoctor(Integer.MIN_VALUE);
        }
    };
}
