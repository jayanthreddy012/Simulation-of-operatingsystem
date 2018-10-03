/**
 * The OUTSPOOLING class consists output function which displays the Pagemaptable, Job ID, termination type, Input,
 * Output, Memory utilization & fragment size, Disk utilization & fragment size of a respective JOB.
 * It consists of global variable map which stores the page-frame of PMT for every 15 clock vtu's
 * memory_util_word_num, memory_util_word_den, percentage variables are used by ratio function to calculate ratios and
 * percentages.
 */
import java.math.BigInteger;
public class OUTPUTSPOOLING {

    static void output(PCB obj) throws ERROR_HANDLER {


        try {
            if (obj != null) {
                int[] page_replace = new int[obj.frame.size()];
                for (int i = 0; i < page_replace.length; i++) {

                    for (int j = 0; j < obj.total_pages; j++) {

                        if (obj.pmt[j].frame_number.equals(Integer.toString(obj.frame.get(i) / 8))) {
                            page_replace[i] = Integer.parseInt(obj.pmt[j].page_number);
                            break;
                        }
                    }
                }
                // writing back the pages in memory to disk which has dirty bit enabled.
                for (int i = 0; i < obj.frame.size(); i++) {

                    if (obj.pmt[page_replace[i]].dirty_bit.equals("1")) {

                        obj.pmt[page_replace[i]].valid_invalid = "0";
                        obj.pmt[page_replace[i]].frame_number = "";
                        obj.pmt[page_replace[i]].dirty_bit = "0";
                        int disk_index = obj.disk_map.get(Integer.parseInt(obj.pmt[page_replace[i]].page_number));
                        int k = obj.frame.get(i);
                        for (int q = disk_index; q < disk_index + 8; q++) {
                            if (SYSTEM.memory_variable[k].equals("")) {
                                SYSTEM.disk[q] = "";
                            } else {
                                SYSTEM.disk[q] = Hextoany.Binarytohex(SYSTEM.memory_variable[k]);
                            }
                            k++;
                        }

                    }

                }
                if (obj.exit_flag == 1) {
                    for (int i = 0; i < obj.frame.size(); i++) {

                        int x = obj.frame.get(i);
                        for (int j = x; j < x + 8; j++) {
                            if (SYSTEM.memory_variable[j] == null) {
                                continue;
                            }
                            if (SYSTEM.memory_variable[j].equals("")) {

                                obj.memory_fragment_size++;
                            }


                        }
                    }

                }


                SYSTEM.exe.println("\n\n\n\nJOB ID : " + obj.job_id + "  (HEX)");
                if (obj.warningmsg != null) {
                    SYSTEM.exe.println("Termination type : " + obj.termtype + " : " + obj.warningmsg);
                }
                if (obj.termmsg != null) {
                    SYSTEM.exe.println("Termination type :" + obj.termtype + " : " + obj.termmsg);}
                    SYSTEM.exe.print("INPUT SEGMENT DATA FOR JOB " + obj.job_id + " (HEX) : ");
                    for (String s : obj.input
                            ) {
                        SYSTEM.exe.print(s + ",");
                    }
                    SYSTEM.exe.println("");
                    SYSTEM.exe.print("OUTPUT SEGMENT DATA FOR JOB " + obj.job_id + "(BIN) : ");
                    for (String s : obj.output
                            ) {
                        SYSTEM.exe.print(s + ",");
                    }
                    SYSTEM.exe.println("\nCLOCK              : " + Integer.toHexString(SYSTEM.clock) + "  (HEX)" + SYSTEM.clock +
                            "\nARRIVAL TIME       : " + (obj.arrivaltime) + " (DEC)" +
                            "\nDEPARTURE TIME       : " + (obj.departuretime) + " (DEC)" +
                            "\nJOB RUN TIME       : " + (obj.run_time) + " (DEC)" +
                            "\nEXECUTION TIME     : " + (obj.run_time - obj.clock1 - obj.segment_fault_time -
                            obj.page_fault_time) + " (DEC)" +
                            "\nINPUT/OUTPUT TIME  : " + obj.clock1 + "  (DEC)" +
                            "\nNO OF PAGEFAULTS  : " + obj.no_pagefault + "  (DEC)" +
                            "\nNO OF SEGMENT FAULTS  : " + obj.no_segmentfault + "  (DEC)" +
                            "\nSEGMENT FAULT TIME : " + obj.segment_fault_time + "  (DEC)" +
                            "\nPAGE FAULT TIME    : " + obj.page_fault_time + "  (DEC)" + ratio(SYSTEM.memory_variable.length,
                            (obj.frame.size() * 8) - (obj.memory_fragment_size), obj) +
                            "\nMEMORY UTILIZATION IN WORDS : RATIO  : " + obj.memory_util_word_num + "/" + obj.memory_util_word_den
                            + " PERCENTAGE  : " + SYSTEM.decformat.format(obj.percentage) + " %"
                            + ratio(SYSTEM.memory_variable.length / 8, (obj.frame.size()), obj) +
                            "\nMEMORY UTILIZATION IN FRAMES : RATIO : " + obj.memory_util_word_num + "/" + obj.memory_util_word_den
                            + " PERCENTAGE    : " + SYSTEM.decformat.format(obj.percentage) + " %" + ratio(SYSTEM.disk.length, ((obj.total_pages * 8) -
                            (obj.disk_fragment_size)), obj) +
                            "\nDISK UTILIZATION IN WORDS :   RATIO  : " + obj.memory_util_word_num + "/" + obj.memory_util_word_den
                            + " PERCENTAGE : " +SYSTEM.decformat.format(obj.percentage) + " %" + ratio(SYSTEM.disk.length / 8, ((obj.total_pages)), obj) +
                            "\nDISK UTILIZATION IN FRAMES :   RATIO : " + obj.memory_util_word_num + "/" + obj.memory_util_word_den
                            + " PERCENTAGE  : " + SYSTEM.decformat.format(obj.percentage) + " %" +
                            "\nMEMORY FRAGMENT SIZE : " + ((obj.exit_flag == 1) ? obj.memory_fragment_size + "  (DEC)" : " " +
                            "Not yet calculated") +
                            "\nDISK FRAGMENT SIZE   : " + ((obj.exit_flag == 1) ? obj.disk_fragment_size / 3.0 + "  (DEC)"
                            : " Not yet calculated ") +
                            "\nTURN AROUND TIME : " + (Math.abs(obj.departuretime+obj.clock1 - obj.arrivaltime)) + "  (DEC)");



//destroy jobs PCB and release respective memory and disk pages


                if (obj.exit_flag == 1) {
                    for (int i = 0; i < obj.frame.size(); i++) {

                        int x = obj.frame.get(i);
                        SYSTEM.fmbv[x / 8] = 0;

                        for (int k = x; k < x + 8; k++) {

                            SYSTEM.memory_variable[k] = null;


                        }
                    }


                    for (int i = 0; i < obj.frame_disk.size(); i++) {

                        int x = obj.frame_disk.get(i);
                        SYSTEM.fmbv_disk[x / 8] = 0;

                        for (int k = x; k < x + 8; k++) {

                            SYSTEM.disk[k] = null;


                        }
                    }


                }


            }

        } catch (Exception e) {
            throw new ERROR_HANDLER(9);
        }

    }


    static String ratio(int x, int y, PCB obj) {
        BigInteger word = BigInteger.valueOf(x);
        int util = y;
        BigInteger frag = BigInteger.valueOf(util);
        BigInteger gc = frag.gcd(word);
        BigInteger numerator = frag.divide(gc);
        BigInteger denominator = word.divide(gc);
        obj.memory_util_word_num = numerator;
        obj.memory_util_word_den = denominator;
        String val = "100";
        BigInteger value = new BigInteger(val);
        BigInteger percent = numerator.multiply(value);
        obj.percentage = percent.doubleValue() / denominator.doubleValue();
        return "";
    }

    static void snapshot_fun(PCB obj) {

        SYSTEM.snapshot_s++;
        int mem_util = SYSTEM.fmbv.length;
        int memory_utilization = 0;
        while (mem_util > 0) {
            if (SYSTEM.fmbv[mem_util - 1] == 1) {
                int y = (mem_util - 1) * 8;
                for (int i = y; i < y + 8; i++) {
                    if (!("").equals(SYSTEM.memory_variable[i])) {
                        memory_utilization++;
                    }
                }
            }

            mem_util--;
        }

        int mem_util_disk = SYSTEM.fmbv_disk.length;
        int disk_utilization = 0;
        while (mem_util_disk > 0) {
            if (SYSTEM.fmbv_disk[mem_util_disk - 1] == 1) {
                int y = (mem_util_disk - 1) * 8;
                for (int i = y; i < y + 8; i++) {
                    if (!("").equals(SYSTEM.disk[i])) {
                        disk_utilization++;
                    }
                }
            }

            mem_util_disk--;
        }

        int mega = SYSTEM.ready_queue.size();
        int last_size = SYSTEM.blocked_queue.size();
        SYSTEM.exe.print("\n-----------------------------------------" +
                "\n\n -----------------------SNAPSHOT---------------\n" +
                "\nCONTENTS OF READY QUEUE         (DEC): ");
        if(mega==0){SYSTEM.exe.print(" NO ELEMENT");}
        while (mega > 0) {
            int x = SYSTEM.ready_queue.poll();
            SYSTEM.exe.print(x + ",");
            SYSTEM.ready_queue.add(x);
            mega--;
        }
        SYSTEM.exe.print(

                "\nJOB ID CURRENTLY EXECUTING      (DEC): " + SYSTEM.job_id.get(obj));

        SYSTEM.exe.println("\nPMT  ");
        SYSTEM.exe.println("Page number  frame number");
        for (int i = 0; i < obj.frame.size(); i++) {

            for (int j = 0; j < obj.total_pages; j++) {

                if (obj.pmt[j].frame_number.equals(Integer.toString(obj.frame.get(i) / 8))) {

                    SYSTEM.exe.println("    " + obj.pmt[j].page_number + "      -     " + obj.pmt[j].frame_number);
                    break;
                }
            }
        }
        SYSTEM.exe.print("\nCONTENTS OF BLOCKED QUEUE       (DEC): ");
        if(last_size==0){SYSTEM.exe.print(" NO ELEMENT");}
        while (last_size > 0) {
            int x = SYSTEM.blocked_queue.poll();
            SYSTEM.exe.print(x + ",");
            SYSTEM.blocked_queue.add(x);
            last_size--;
        }
        SYSTEM.exe.print(
                "\nDEGREE OF MULTIPROGRAMMING      (DEC): " + (SYSTEM.ready_queue.size() + SYSTEM.blocked_queue.size()) +
                        "\nMEMORY UTILIZATION              (DEC): " + SYSTEM.decformat.format((memory_utilization / (32.0 * 8.0)) * 100) + "%" +
                        "\nDISK UTILIZATION                (DEC): " + SYSTEM.decformat.format((disk_utilization / (256.0 * 8.0)) * 100) + "%" +
                        "\n----------------------------------------------\n");


        SYSTEM.memory_util_mean= (int) (SYSTEM.memory_util_mean+(memory_utilization / (32.0 * 8.0)) * 100);
        SYSTEM.disk_util_mean= (int) (SYSTEM.disk_util_mean+(disk_utilization / (256.0 * 8.0)) * 100);
    }


    static void executionprofile() {
        int x = SYSTEM.infinite_id.size();

        SYSTEM.exe.println(
                "\n--------------------------------------------" +



                        "\nCLOCK             (HEX): " + Integer.toHexString(SYSTEM.clock) +
                        "\n\n METERING AND REPORTING \n" +
                        "--------------------------------------------------" +
                        "\nNUMBER OF JOBS PROCESSED         (DEC) : " + SYSTEM.jid +
                        "\n\nCPU TIME FOR JOBS THAT TERMINATE NORMALLY " +
                        "\n---MINIMUM       (DEC): " + SYSTEM.cpu_min_time + " " +
                        "\n---MAXIMUM       (DEC): " + SYSTEM.cpu_max_time + " " +
                        "\n---AVERAGE       (DEC): " + SYSTEM.decformat.format(SYSTEM.cpu_avg_time) + " " +
                        "\n\nTURNAROUND TIME FOR ALL THE JOBS " +
                        "\n---MINIMUM       (DEC): " + SYSTEM.ta_min_time + " " +
                        "\n---MAXIMUM       (DEC): " + SYSTEM.ta_max_time + " " +
                        "\n---AVERAGE       (DEC): " + SYSTEM.decformat.format(SYSTEM.ta_avg_time) + " " +
                        "\n\nCODE SEGMENT SIZE " +
                        "\n-IN BATCH PACKET       " +
                        "\n---MINIMUM       (DEC): " + SYSTEM.code_min_batch + " " +
                        "\n---MAXIMUM       (DEC): " + SYSTEM.code_max_batch + " " +
                        "\n---AVERAGE       (DEC): " + SYSTEM.decformat.format(SYSTEM.code_avg_batch) + " " +
                        "\n-AS COMPUTED BY LOADER       " +
                        "\n---MINIMUM       (DEC): " + SYSTEM.code_min_batch + " " +
                        "\n---MAXIMUM       (DEC): " + SYSTEM.code_max_batch + " " +
                        "\n---AVERAGE       (DEC): " + SYSTEM.decformat.format(SYSTEM.code_avg_batch) + " " +
                        "\n\nINPUT SEGMENT SIZE " +
                        "\n-IN BATCH PACKET       " +
                        "\n---MINIMUM       (DEC): " + SYSTEM.input_min_batch + " " +
                        "\n---MAXIMUM       (DEC): " + SYSTEM.input_max_batch + " " +
                        "\n---AVERAGE      (DEC) : " + SYSTEM.decformat.format(SYSTEM.input_avg_batch) + " " +
                        "\n-AS COMPUTED BY LOADER       " +
                        "\n---MINIMUM       (DEC): " + SYSTEM.input_min_loader + " " +
                        "\n---MAXIMUM       (DEC): " + SYSTEM.input_max_loader + " " +
                        "\n---AVERAGE       (DEC): " + SYSTEM.decformat.format(SYSTEM.input_avg_loader) + " " +
                        "\n\nOUTPUT SEGMENT SIZE " +
                        "\n-IN BATCH PACKET       " +
                        "\n---MINIMUM      (DEC) : " + SYSTEM.output_min_batch + " " +
                        "\n---MAXIMUM       (DEC): " + SYSTEM.output_max_batch + " " +
                        "\n---AVERAGE       (DEC): " + SYSTEM.decformat.format(SYSTEM.output_avg_batch) + " " +
                        "\n-AS COMPUTED BY LOADER       " +
                        "\n---MINIMUM       (DEC): " + SYSTEM.output_min_loader + " " +
                        "\n---MAXIMUM       (DEC): " + SYSTEM.output_max_loader + " " +
                        "\n---AVERAGE       (DEC): " + SYSTEM.decformat.format(SYSTEM.output_avg_loader) + " " +
                        "\n\nCPU SHOTS " +
                        "\n---MINIMUM       (DEC): " + SYSTEM.cpushot_min + " " +
                        "\n---MAXIMUM       (DEC): " + SYSTEM.cpushot_max + " " +
                        "\n---AVERAGE       (DEC): " + SYSTEM.decformat.format(SYSTEM.cpushot_avg) + " " +
                        "\n\nI/0 REQUESTS " +
                        "\n---MINIMUM       (DEC): " + SYSTEM.io_min + " " +
                        "\n---MAXIMUM       (DEC): " + SYSTEM.io_max + " " +
                        "\n---AVERAGE       (DEC): " + SYSTEM.decformat.format(SYSTEM.io_avg) + " \n" +
                        "----------------------------------------------------------\n" +

                        "-----------------------------------------------------------" +
                        "\n\nJOBS THAT TERMINATED NORMALLY (DEC): " + SYSTEM.normal_termination + " \n" +
                        "\n\nJOBS THAT TERMINATED ABNORMALLY (DEC): " + SYSTEM.abnormal_termination + " \n" +
                        "\n\nTIME LOST DUE TO ABNORMAL TERMINATION OF JOBS :" + SYSTEM.time_abnormal_termination + " \n" +
                        "\n\nTIME LOST DUE SUSPECTED INFINITE JOBS (DEC):" + SYSTEM.suspected_infinite + " (DEC)\n" +
                        "\n\nID'S OF JOBS SUSPECTED INFINITE (DEC):");
        for (int i = 0; i < x; i++) {
            SYSTEM.exe.print(SYSTEM.infinite_id.get(0) + ",");
        }

        SYSTEM.exe.print(

                "\n\nMEAN TURN AROUND TIME OF JOBS THAT TERMINATED NORMALLY (DEC): " + SYSTEM.decformat.format(SYSTEM.mean_tat / SYSTEM.jid) + " \n" +
                        "\n\nMEAN WAITING TIME OF JOBS THAT TERMINATED NORMALLY (DEC): " + SYSTEM.decformat.format(SYSTEM.mean_wt / SYSTEM.jid) + " \n" +
                        "\n\nMEAN NUMBER OF PAGEFAULTS (DEC): " + SYSTEM.decformat.format(SYSTEM.pagefault_value / SYSTEM.jid) + " \n" +
                        "\n\nMEAN MEMORY UTILIZATION OVER ALL SAMPLING INTERVALS (DEC): " + SYSTEM.decformat.format(SYSTEM.memory_util_mean/SYSTEM.snapshot_s) + "% \n" +
                        "\n\nMEAN DISK UTILIZATION OVER ALL SAMPLING INTERVALS (DEC): "+SYSTEM.decformat.format(SYSTEM.disk_util_mean/SYSTEM.snapshot_s) + " %\n"

        );
        SYSTEM.exe.close();

    }

}


