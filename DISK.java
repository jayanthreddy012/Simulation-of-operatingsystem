/**
 * The DISK module is used by the SYSTEM, LOADER, CPU to spool the data of the input.txt jobs and read the data respectively.
 * It consists of fmbv_disk and disk as global variables which specifies the available disk frame to spool the job data
 * and disk is used to store and read the data.
 */
public class DISK {


    public static String disk_fun(String x, String y, String z) throws ERROR_HANDLER {
        // read the data from the disk
        try{
        if (x.equals("read")) {
            return (SYSTEM.disk[Integer.parseInt(y)]);

        }
        //write data to the disk
        else if (x.equals("write")) {

            SYSTEM.disk[Integer.parseInt(y)] = z;
        } else {
            throw new ERROR_HANDLER(15);
        }
        return "";
    }catch (Exception e){
            throw new ERROR_HANDLER(15);

        }

    }

}


