/** ERROR_HANDLER class is called once an error is triggered anywhere in the SYSTEM, MEMORY, CPU
 * Upon receiving the error it returns the error message and the control to the SYSTEM.
 * */


public class ERROR_HANDLER extends Exception  {
    // Constructor which returns the error message to SYSTEM.
    ERROR_HANDLER(int error) {

        super(get_error_msg(error));



    }

    static String get_error_msg(int error) {
        switch (error){
            //if the arguments of the file i.e the file name is wrongly specified
            case 0:
                return "File not found";
            // br not specified properly
            case 1:
                return "Base register overflow error";
            // objpc not specified properly
            case 2:
                return "Program counter overflow error";
            //trace file bit is not entered properly
            case 3:
                return "Trace flag error";
            case 4:
                return "**INPUT is Missing from its place in input.txt job";
            case 5:
                return "**FIN is Missing from its place in input.txt job";
            case 6:
                return "Missing **JOB in input.txt file";
            case 7:
                return "Missing elements in **JOB line in input.txt file";
            case 8:
                return "Input Argument Overflowed";
            case 9:
                return " arguments error";
            case 10:
                return "File not found";
            case 11:
                return "Infinite job error";
            case 12:
                return "Stack underflow error for the job";
            case 13:
                return "Stack overflow error for the job";
            case 14:
                return "Invalid memory operation";
            case 15:
                return "Invalid Disk operation";
            case 16:
                return "Divide by zero exception";
            case 17:
                return "Conflict between no of input.txt words specified in **JOB line of the job and no of input.txt items " +
                        "given in the INPUT section";
            case 18:
                return "Conflict between size of Program words specified and no of program items given in the " +
                        "program section";
            case 19:
                return "Missing/improper Loader format in input.txt file";
            case 20:
                return "More than one **INPUT statement";
            case 21:
                return "Reading beyond the specified Input-Data-segment";
            case 22:
                return "Writing beyond the specified Output-Data-segment";
        }
        return "";
    }
}
