package uk.ncl.CSC8016.jackbergus.coursework.project2.processes;

import java.util.Date;
import java.util.List;

public class ProductMonitorTest {
    public static void main(String[] args) {
        ProductMonitorTest productMonitorTest = new ProductMonitorTest();
        System.out.println();
    }
}
/*
public class LecturerTest {
    public static void main(String[] args) {
        LecturerTest lecturerTest = new LecturerTest();
        System.out.println("Test create Lecturer");
        lecturerTest.createLecturer();
        System.out.println("Test get staff module list");
        lecturerTest.testGetModuleList();
        System.out.println("Test number credits for lecturer");
        lecturerTest.testEnoughCredits();

    }
    private void createLecturer(){
        //test normal case
        String testStatus = "fixed";
        Name testName = new Name("Veronica", "Lodge");
        Date testDate = new Date(1988, 06, 20);
        Lecturer l = new Lecturer(testStatus, testName, testDate);
        Assertions.assertNotNull(l);
        //test exception case, no status
        try {
            String testStatus1 = null;
            Name testName1 = new Name("Veronica", "Lodge");
            Date testDate1 = new Date(1988, 06, 20);
            Lecturer l1 = new Lecturer(testStatus1, testName1, testDate1);
        } catch (Throwable t) {
            Assertions.assertExpectedThrowable(NullPointerException.class, t);
        }
        //test exception case, null first name
        try {
            String testStatus2 = "fixed";
            Name testName2 = new Name(null, "Lodge");
            Date testDate2 = new Date(1988, 06, 20);
            Lecturer l2 = new Lecturer(testStatus2, testName2, testDate2);
        } catch (Throwable t) {
            Assertions.assertExpectedThrowable(IllegalArgumentException.class, t);
        }
        //test exception case, null last name
        try {
            String testStatus3 = "fixed";
            Name testName3 = new Name("Veronica", null);
            Date testDate3 = new Date(1988, 06, 20);
            Lecturer l3 = new Lecturer(testStatus3, testName3, testDate3);
        } catch (Throwable t) {
            Assertions.assertExpectedThrowable(IllegalArgumentException.class, t);
        }
        //test exception case, null DOB
        try {
            String testStatus4 = "fixed";
            Name testName4 = new Name("Veronica", "Lodge");
            Date testDate4 = null;
            Lecturer l4 = new Lecturer(testStatus4, testName4, testDate4);
        } catch (Throwable t) {
            Assertions.assertExpectedThrowable(NullPointerException.class, t);
        }
    }
    private void testGetModuleList(){
        //modules are added in a different class; it will be tested separately
        String testStatus = "permanent";
        Name testName = new Name("Archie", "Andrews");
        Date testDate = new Date(1986, 0, 20);
        Lecturer l = new Lecturer(testStatus, testName, testDate);
        List<Module> testList = l.getStaffModuleList();
        Assertions.assertEquals(testList, l.getStaffModuleList());
    }
    private void testEnoughCredits(){
        //normal case
        String testStatus = "fixed";
        Name testName = new Name("Chuck", "Newman");
        Date testDate = new Date(1958, 3, 2);
        Lecturer l = new Lecturer(testStatus, testName, testDate);
        Module testMod = new Module("CSC8014", "Testing", 1, 5);
        l.getStaffModuleList().add(testMod);
        boolean testCredits = l.enoughCredits(l);
        Assertions.assertEquals(false, testCredits);
        //add too many test credits, normal case (expect true)
        Module tooBigTestMod = new Module("CSC8018", "TestingBig", 2, 45);
        boolean tooManyCred = l.getStaffModuleList().add(tooBigTestMod);
        Assertions.assertEquals(true, tooManyCred);
    }
}

 */

