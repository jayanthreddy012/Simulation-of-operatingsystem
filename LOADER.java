/**
 * Upon the SYSTEM's order LOADER loads the user job to memory. SYSTEM sends the base address, trace_flag
 * as parameters which helps LOADER in loading the program in the MEMORY.
 */
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

class LOADER {

    @SuppressWarnings("unchecked")
    public static void Loader_fun(String br, int trace_flag, PCB a) throws ERROR_HANDLER, IOException {
        if (a.trace_flag.equals("1")) {
            a.pw = new PrintWriter(new FileWriter(System.getProperty("user.dir") + "/tracefile_" + a.given_id+"_"+a.job_id+".txt"));
        }
        if (a.trace_flag.equals("1")) {
            a.pw.println(String.format(
                    "%30s%17s%10s%15s\r\n", "", "Before  execution", "",
                    "After  execution"));
            a.pw.println(String.format(
                    "%7s%8s%6s%8s%7s%7s%7s%7s%7s%7s%7s\r\n", "HEX",
                    "HEX", "HEX", "HEX", " HEX", "HEX", " HEX",
                    "HEX", " HEX", "HEX", " HEX"));
            a.pw.println(String.format(
                    "%7s%8s%6s%8s%7s%7s%7s%7s%7s%7s%7s\r\n", "PC",
                    "BR", "IR", "TOS", "S[TOS]", "EA", "(EA)",
                    "TOS", "S[TOS]", "EA", "(EA)"));
        }
         int initial_load, k = 0, initial_availableframe=0,  initial_pageno, frame_address;
         String[] word = new String[8];


        initial_load = (a.disk_map.get(a.pc/8)) ;
        k=0;
        for (int i = initial_load; i < initial_load + 8; i++) {
            word[k] = DISK.disk_fun("read", Integer.toString(i), "");
            word[k] = Hextoany.hexToBinary(word[k]);
            k++;
        }

        // the page is loaded into the available memory frame
        for (int i = 0; i < SYSTEM.fmbv.length; i++) {
            if (SYSTEM.fmbv[i] == 0) {
                SYSTEM.fmbv[i] = 1;
                initial_availableframe = i;
                break;
            }
        }
        a.program_segment_pointer = 0;
        a.pcb_fun();
        a.pmt[a.pc/8].page_number = Integer.toString(a.pc/8);
        a.pmt[a.pc/8].frame_number = Integer.toString(initial_availableframe);
        a.pmt[a.pc/8].valid_invalid = "1";
        frame_address = (initial_availableframe * 8) + Integer.parseInt(a.br);
        k = 0;
        for (int i = frame_address; i < frame_address + 8; i++) {
            SYSTEM.memory_variable[i] = word[k];
            k++;
        }
        a.frame_count++;
        a.frame.add(frame_address);
        int totpage = a.no_of_frames, avav = 0;
        // the remaining frame base address for the job is stored in frame of PCB
        while (totpage > 1) {
            for (int i = 0; i < SYSTEM.fmbv.length; i++) {
                if (SYSTEM.fmbv[i] == 0) {
                    SYSTEM.fmbv[i] = 1;
                    avav = i;
                    break;
                }
            }

            a.frame.add(avav * 8);
            totpage--;
        }


    }

}