/**
 * The MEMORY module is used by the LOADER, CPU to read or write instructions.
 * memory_variable and fmbv are the Global variables
 * memory variable stores the job instructions and fmbv specifies the available memory frame.
 */
class MEMORY {



    static String Memory_function(String x, String y, String z, PCB obj) throws ERROR_HANDLER {
        int ea;
        try {
            // read from memory
            if (x.equals("read")) {
                ea = (obj.disk_map.get(Hextoany.bintodecimal(y) / 8)) / 8;
                // checks whether the page is present in the memory if not it causes a pagefault
                //and the control is transferred back to the CPU which in turn transfers to SYSTEM.
                if (obj.pmt[(Hextoany.bintodecimal(y) / 8)].valid_invalid.equals("0")) {
                    obj.current_page = (Hextoany.bintodecimal(y) / 8);
                    obj.flag_pagefault = 1;
                    obj.ea_old = ea;

                    return "pagefault";

                } else {
                    int frame_no = Integer.parseInt(obj.pmt[(Hextoany.bintodecimal(y) / 8)].frame_number);
                    obj.pmt[(Hextoany.bintodecimal(y) / 8)].reference_bit = "1";
                    ea = frame_no * 8 + (Hextoany.bintodecimal(y) % 8);
                    return SYSTEM.memory_variable[ea];
                }


            }
            // write a word to memory
            else if (x.equals("write")) {

                ea = (obj.disk_map.get(Integer.parseInt(y) / 8)) / 8;
                // checks whether the page is present in the memory if not it causes a pagefault
                //and the control is transferred back to the CPU which in turn transfers to SYSTEM.
                if (obj.pmt[(Integer.parseInt(y) / 8)].valid_invalid.equals("0")) {
                    obj.current_page = (Integer.parseInt(y) / 8);
                    obj.flag_pagefault = 1;
                    obj.ea_old = ea;

                    return "pagefault";
                } else {
                    int frame_no = Integer.parseInt(obj.pmt[(Integer.parseInt(y) / 8)].frame_number);
                    obj.pmt[(Integer.parseInt(y) / 8)].dirty_bit = "1";
                    ea = frame_no * 8 + (Integer.parseInt(y) % 8);
                    return SYSTEM.memory_variable[ea] = z;
                }


            }
        } catch (Exception e) {
            throw new ERROR_HANDLER(14);
        }

        return "";
    }


}