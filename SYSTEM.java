/**
 * BUJULA JAYANTH REDDY
 * CS5323
 * A Simple Batch System
 * 02/27/2018
 * Global Variables:
 * clock - Which stores the total running time of a user job.
 * termtype - Describes termination type of the exception
 * warningmsg - contains the warning message caused by the exception
 * termmsg - contains the termination message caused by exception
 * filereader- used to read the file path
 * bufferedReader- used by file reader to get characters from a file.
 * output_flag- It is used to determine available frame in disk.
 * input_flag- It is used to determine the available frame in memory.
 * ready_queue- stores the processes ids that are ready to execute
 * The SYSTEM class is the heart of the simple batch batch system that is designed.
 * It controls all the other components like LOADER, MEMORY, CPU, ERROR_HANDLER.
 * It continuously checks whether if there are any user jobs and calls loader to load the jobs to MEMORY if present
 * and calls CPU for execution of the job.
 */

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;


public class SYSTEM {
    static BufferedReader bufferedReader;
    static String file;
    static int clock = 0,snapshot=0, memoryaddress, error = 0, loader_flag = 1, jid = 0, available_memory_size = 32, count_inputfile = 0, count_halt = 0, haltflag = 0, disk_page_count = 0, finale = 0;
    static HashMap<Integer, PCB> pcb = new HashMap<>();
    static HashMap<PCB, Integer> job_id = new HashMap<>();
    static PCB[] aa = new PCB[100000];
    static Queue<Integer> ready_queue = new LinkedList<>();
    static Queue<Integer> blocked_queue = new LinkedList<>();
    static String[] memory_variable = new String[256];
    static int[] fmbv = new int[32];
    static int[] fmbv_disk = new int[256];
    static String[] disk = new String[2048];
    static PCB currentjob, currentjob2;
    static PrintWriter exe;
    static int noofjobs = 0,tat=0,normal_tat=0,normal_wt;
    static int cpu_min_time = 0;
    static int cpu_max_time = 0;
    static double cpu_avg_time = 0;
    static int ta_min_time = 0;
    static int ta_max_time = 0;
    static double ta_avg_time = 0;
    static int code_min_batch = 0;
    static int code_max_batch = 0;
    static double code_avg_batch = 0;
    static int input_min_batch = 0;
    static int input_max_batch = 0;
    static double input_avg_batch = 0;
    static int input_min_loader = 0;
    static int input_max_loader = 0;
    static double input_avg_loader = 0;
    static int output_min_batch = 0;
    static int output_max_batch = 0;
    static double output_avg_batch = 0;
    static int output_min_loader = 0;
    static int output_max_loader = 0;
    static double output_avg_loader = 0;
    static int cpushot_min = 0;
    static int cpushot_max = 0,clock_idle=0;
    static double cpushot_avg = 0, mean_pagefault_value=0;
    static int io_min = 0, pagefault_value=0;
    static int io_max = 0, normal_termination=0,abnormal_termination=0, time_abnormal_termination=0, suspected_infinite=0;
    static double io_avg = 0, mean_tat=0, mean_wt=0;
    static ArrayList<Integer> infinite_id= new ArrayList<>();
    static DecimalFormat decformat= new DecimalFormat("0.00");
    static int snapshot_s = 0;
    static int memory_util_mean=0, disk_util_mean=0;

    public static void main(String[] args) throws ERROR_HANDLER, IOException {

        // checks whether an argument is passed to the SYSTEM
         file=args[0];
        try {
            bufferedReader = new BufferedReader(new FileReader(
                    file));

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.exit(0);

        }
        exe = new PrintWriter(new FileWriter(System.getProperty("user.dir") + "/Execution_profiledemo.txt"),true);
        Inputspooling(bufferedReader);
        while (true) {
            try {

                if ((ready_queue.size() == 0 && blocked_queue.size() == 0) || (haltflag == 1 && finale != 1) || (error == 1 && finale != 1)) {
                    String read;
                    error = 0;
                    if ((read = bufferedReader.readLine()) == null) {
                        break;
                    }
                    count_inputfile++;

                    bufferedReader = new BufferedReader(new FileReader(
                            file));
                    String read1 = "";
                    count_inputfile = count_inputfile - 3;
                    for (int i = 0; i < count_inputfile; i++) {
                        read1 = bufferedReader.readLine();
                    }
                    Inputspooling(bufferedReader);

                }


                while (ready_queue.size() == 0 && blocked_queue.size() > 0) {
                    clock_idle++;
                    clock++;
                    blockedcheck();
                }
                // if(ready_queue.size()<=0) {System.exit(0);}
                String job_element = String.valueOf(ready_queue.poll());


                PCB a = pcb.get(Integer.parseInt(job_element));
                currentjob = a;
                if (loader_flag == 0 && a.loader_flag == 0) {
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
                    int avav = 0;
                    // the remaining frame base address for the job is stored in frame of PCB
                    while (a.totpage > 0) {
                        for (int i = 0; i < SYSTEM.fmbv.length; i++) {
                            if (fmbv[i] == 0) {
                                fmbv[i] = 1;
                                avav = i;
                                break;
                            }
                        }

                        a.frame.add(avav * 8);
                        a.totpage--;
                    }
                    a.pcb_fun();
                    a.loader_flag = 1;

                } else if (loader_flag == 1) {
                    a.loader_flag = 1;
                    LOADER.Loader_fun(a.br, (Integer.parseInt(a.trace_flag)), a);
                    loader_flag = 0;
                }


                PCB x;


                x = CPU.cpu_fun(Integer.toString(a.pc), (Integer.parseInt(a.trace_flag)), a);

                while (true) {
                    //control transfers to system after job releasing the cpu when a pagefault occur
                    if (x.flag_pagefault == 1) {
                        pagefault_value++;
                        x.no_pagefault++;
                        mean_pagefault_value=mean_pagefault_value+(pagefault_value/2.0);
                        x.pc = x.pc_old;
                        blocked_queue.add(job_id.get(x));
                        x.blocked_status = "blocked";
                        x.blocked_time = clock + x.segment_fault_time + x.page_fault_time;
                        FAULT_HANDLER.page_fault_handler(x.ea_old, x);
                        x.flag_pagefault = 0;
                        blockedcheck();
                        break;
                    }

                    //job releasing cpu after a input.txt segment fault
                    if (x.flag_segmentfault_input == 1) {
                        pagefault_value++;
                        x.no_segmentfault++;
                        mean_pagefault_value=mean_pagefault_value+(pagefault_value/2.0);
                        x.pc = x.pc_old;
                        blocked_queue.add(job_id.get(x));
                        x.blocked_status = "blocked";
                        x.blocked_time = clock + x.segment_fault_time + x.page_fault_time;
                        FAULT_HANDLER.segment_fault_handler(x, "input.txt");
                        blockedcheck();
                        break;
                    }
                    //job releasing cpu after output segment fault
                    if (x.flag_segmentfault_output == 1) {
                        x.no_segmentfault++;
                        pagefault_value++;
                        mean_pagefault_value=mean_pagefault_value+(pagefault_value/2.0);
                        x.pc = x.pc_old;
                        blocked_queue.add(job_id.get(x));
                        x.blocked_status = "blocked";
                        x.blocked_time = clock + x.segment_fault_time + x.page_fault_time;
                        FAULT_HANDLER.segment_fault_handler(x, "output");
                        blockedcheck();
                        break;
                    }

                    if (x.execution_flag == 1) {
                        x.execution_flag = 0;
                        x.execution_time_in_cpu = 0;
                        int jobid = job_id.get(x);
                        ready_queue.add(jobid);
                        blockedcheck();
                        break;
                    }
                    //exiting jobs from CPU.
                    if (x.halt_flag == 1) {
                        x.departuretime=clock;
                        x.executiontime=x.run_time - x.clock1 - x.segment_fault_time -
                                x.page_fault_time;
                        normal_tat=tat=(clock+x.page_fault_time+x.segment_fault_time+ x.clock1)-x.arrivaltime;
                        mean_tat=mean_tat+normal_tat;
                        normal_wt=normal_wt+(normal_tat- x.clock1 - x.segment_fault_time -
                                x.page_fault_time);
                        mean_wt=mean_wt+(normal_tat-x.executiontime);
                        meter(x);
                        meter2(x);
                        normal_termination++;
                        haltflag = 1;
                        available_memory_size = available_memory_size + x.no_of_frames;
                        count_halt++;

                        // checking the jobs present in blocked queue.
                        blockedcheck();
                        //spooling more jobs

                        break;
                    }

                }

            }
            // It catches all the exceptions thrown by all the components in the Simple Batch System.
            catch (Exception e) {
                if (currentjob != null) {
                    abnormal_termination++;
                    currentjob.departuretime=clock;
                    tat=(clock+currentjob.page_fault_time+currentjob.segment_fault_time)-currentjob.arrivaltime;
                    meter2(currentjob);
                    suspected_infinite=suspected_infinite+currentjob.run_time-currentjob.page_fault_time-currentjob.segment_fault_time-currentjob.clock1;
                    time_abnormal_termination=time_abnormal_termination+currentjob.run_time;
                    available_memory_size = available_memory_size + currentjob.no_of_frames;
                    error = 1;
                    currentjob.termtype = "ABNORMAL";
                    currentjob.warningmsg = e.toString();
                    currentjob.exit_flag = 1;
                    OUTPUTSPOOLING.output(currentjob);
                    ready_queue.remove(job_id.get(currentjob));
                    blocked_queue.remove(job_id.get(currentjob));

                }
            }
        }
        OUTPUTSPOOLING.executionprofile();

    }

    public static void meter(PCB p) {
        if (cpu_min_time == 0) {
            cpu_min_time = p.executiontime;
        }
        if (cpu_max_time == 0) {
            cpu_max_time = p.executiontime;
        }
        if (p.executiontime < cpu_min_time) {
            cpu_min_time = p.executiontime;
        }
        if (p.executiontime > cpu_min_time) {
            cpu_max_time = p.executiontime;
        }
        cpu_avg_time = (p.executiontime + cpu_avg_time) / 2.0;
    }

    public static void meter2(PCB p) {
        if(ta_min_time==0){ta_min_time=tat;}
        if(ta_max_time==0){ta_max_time=tat;}
        if(tat<ta_min_time){ta_min_time=tat;}
        if(tat>ta_max_time){ta_max_time=tat;}
        ta_avg_time=tat+ta_avg_time/2.0;

        if(code_min_batch==0){code_min_batch=p.program_segment_size;}
        if(code_max_batch==0){code_max_batch=p.program_segment_size;}
        if(p.program_segment_size<code_min_batch){code_min_batch=p.program_segment_size;}
        if(p.program_segment_size>code_max_batch){code_max_batch=p.program_segment_size;}
        code_avg_batch=code_avg_batch+p.program_segment_size/2.0;

        if(input_min_batch==0){input_min_batch=p.input_segment_size;}
        if(input_max_batch==0){input_max_batch=p.input_segment_size;}
        if(p.input_segment_size<input_min_batch){input_min_batch=p.input_segment_size;}
        if(p.input_segment_size>input_max_batch){input_max_batch=p.input_segment_size;}
        input_avg_batch=input_avg_batch+p.input_segment_size/2.0;

        if(input_min_loader==0){input_min_loader=p.input_size;}
        if(input_max_loader==0){input_max_loader=p.input_size;}
        if(p.input_size<input_min_loader){input_min_loader=p.input_size;}
        if(p.input_size>input_max_loader){input_max_loader=p.input_size;}
        input_avg_loader=input_avg_loader+p.input_size/2.0;


        if(output_min_batch==0){output_min_batch=p.output_segment_size;}
        if(output_max_batch==0){output_max_batch=p.output_segment_size;}
        if(p.output_segment_size<output_min_batch){output_min_batch=p.output_segment_size;}
        if(p.output_segment_size>output_max_batch){output_max_batch=p.output_segment_size;}
        output_avg_batch=output_avg_batch+p.output_segment_size/2.0;


        if(output_min_loader==0){output_min_loader=p.output_size;}
        if(output_max_loader==0){output_max_loader=p.output_size;}
        if(p.output_size<output_min_loader){output_min_loader=p.output_size;}
        if(p.output_size>output_max_loader){output_max_loader=p.output_size;}
        output_avg_loader=output_avg_loader+p.output_size/2.0;

        if(cpushot_min==0){cpushot_min=p.cpushot;}
        if(cpushot_max==0){cpushot_max=p.cpushot;}
        if(p.cpushot<cpushot_min){cpushot_min=p.cpushot;}
        if(p.cpushot>cpushot_max){cpushot_max=p.cpushot;}
        cpushot_avg=p.cpushot+cpushot_avg/2.0;

        if(io_min==0){io_min=p.ioshot;}
        if(io_max==0){io_max=p.ioshot;}
        if(p.ioshot<io_min){io_min=p.ioshot;}
        if(p.ioshot>io_max){io_max=p.ioshot;}
        io_avg=p.ioshot+io_avg/2.0;


    }

    public static void blockedcheck() {
        int mega = blocked_queue.size();
        block();
        while (mega > 0) {
            PCB a = pcb.get(blocked_queue.poll());
            if (a.blocked_status.equals("ready")) {

                ready_queue.add(job_id.get(a));
            } else {
                blocked_queue.add(job_id.get(a));
            }

            mega--;
        }

    }

    public static void block() {

        int last_size = blocked_queue.size();
        while (last_size > 0) {
            PCB a = pcb.get(blocked_queue.poll());
            if (((clock + a.page_fault_time + a.segment_fault_time) - a.blocked_time) > 20) {
                a.blocked_status = "ready";
            }
            blocked_queue.add(job_id.get(a));
            last_size--;
        }
    }

    public static void Inputspooling(BufferedReader bufferedReader) throws ERROR_HANDLER, IOException {


        PCB a;
        String[] word = new String[8];
        int size;
        String firstline;
        while (true) {
            try {
                firstline = bufferedReader.readLine();
                count_inputfile++;
                if (firstline != null) {
                    String[] frstline;
                    frstline = firstline.split(" ");


                    jid = jid + 1;
                    a = aa[jid] = new PCB();
                    pcb.put(jid, aa[jid]);
                    job_id.put(a, jid);
                    a.job_id = jid;
                    currentjob2 = a;
                    a.internal_jid=jid;
                    if (frstline.length != 3) {

                        //if the first line of job is not defined properly it throws an error
                        throw new ERROR_HANDLER(7);
                    }

                    //checks for**JOB- Input spooling starts here
                    if (!frstline[0].equals("**JOB")) {

                        //if the first line of job is not defined properly it throws an error
                        throw new ERROR_HANDLER(6);
                    }


                    a.input_segment_size = Integer.parseInt(Hextoany.hexTodecimal(frstline[1]));
                    a.output_segment_size = Integer.parseInt(Hextoany.hexTodecimal(frstline[2]));
                    count_inputfile++;
                    firstline = bufferedReader.readLine();
                    frstline = firstline.split(" ");
                    if (frstline.length != 5) {
                        //if the first line of job is not defined properly it throws an error

                        throw new ERROR_HANDLER(19);
                    }
                    a.program_segment_size = Integer.parseInt(Hextoany.hexTodecimal(frstline[3]));

                    // TODO add jid to ready queue

                    a.given_id=Integer.parseInt(Hextoany.hexTodecimal(frstline[0]));
                    a.br = (Hextoany.hexTodecimal(frstline[1]));
                    //if br exceeds it's limit it throws an error to the ERROR_HANDLER class
                    if (Integer.parseInt(a.br) > 255) {
                        throw new ERROR_HANDLER(1);
                    }
                    a.pc_old = a.pc = Integer.parseInt(Hextoany.hexTodecimal(frstline[2]));
                    //if objpc exceeds it's limit it throws an error to the ERROR_HANDLER class
                    if ((a.pc) > 127) {
                        throw new ERROR_HANDLER(2);
                    }

                    size = a.program_segment_size;
                    if (frstline[4].equals("0") || frstline[4].equals("1")) {
                        a.trace_flag = Hextoany.hexTodecimal(frstline[4]);
                    } else {
                        a.trace_flag = "0";
                        a.warningmsg = "trace flag warning";
                        a.termtype = "Normal";
                    }
                    //if Trace_flag has improper decleration it throws an error to the ERROR_HANDLER class

                    //put no of required program pages in pages present in pcb.
                    a.pages.put("program_page_size", (int) Math.ceil((a.program_segment_size / 8.0)));
                    //put no of required input.txt pages in pages present in pcb.
                    a.pages.put("input_page_size", (int) Math.ceil((a.input_segment_size / 8.0)));
                    //put no of required output pages in pages present in pcb.
                    a.pages.put("output_page_size", (int) Math.ceil((a.output_segment_size / 8.0)));
                    // total pages required for the job

                    String line, line1, input;
                    int program_page = a.pages.get("program_page_size");
                    int input_page = a.pages.get("input_page_size");
                    int input_words = a.input_segment_size;
                    int output_page = a.pages.get("output_page_size");
                    int count_program = 0;
                    a.total_pages = a.pages.get("program_page_size") + a.pages.get("input_page_size") +
                            a.pages.get("output_page_size");
                    if (available_memory_size < a.total_pages) {
                        jid = jid - 1;
                        break;
                    }
                    a.totpage = a.no_of_frames = Math.min(6, a.total_pages);

                    available_memory_size = available_memory_size - a.no_of_frames;
                    a.arrivaltime=clock;
                    ready_queue.add(jid);

                    if (a.trace_flag.equals("1")) {
                        a.pw = new PrintWriter(new FileWriter(System.getProperty("user.dir") + "/tracefile_" + a.given_id+"_"+jid+".txt"));
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
                    //min number of frames allocated to a job
                    //double x=Math.ceil(size/4.0);
                    //count_inputfile= (int) (count_inputfile+x);
                    //reading the program segment from the input.txt job
                    while (size > 0 && (line = bufferedReader.readLine()) != null) {
                        count_inputfile++;
                        int l = 0;
                        char[] ch = line.toCharArray();
                        for (int k = 0; k < 8; k++) {

                            word[k] = "";
                        }
                        for (int i = 0; i < 4; i++) {
                            count_program++;
                            for (int j = l; j < l + 4 && j < ch.length; j++) {

                                word[i] = word[i] + ch[j];

                            }
                            if ((a.program_segment_size >= count_program) && word[i].equals("")) {
                                throw new ERROR_HANDLER(18);
                            }
                            if ((a.program_segment_size < count_program) && !("").equals(String.valueOf(word[i]))) {
                                throw new ERROR_HANDLER(18);
                            }

                            l = l + 4;

                        }
                        size = size - 4;
                        if (size > 0) {
                            line1 = bufferedReader.readLine();
                            count_inputfile++;
                            if ((line1 != null) && (!("**INPUT").equals(line1))) {
                                int l1 = 0;
                                char[] ch1 = line1.toCharArray();
                                for (int i = 4; i < 8; i++) {
                                    count_program++;
                                    for (int j = l1; j < l1 + 4 && j < ch1.length; j++) {

                                        word[i] = word[i] + ch1[j];

                                    }

                                    if ((a.program_segment_size >= count_program) && word[i].equals("")) {
                                        throw new ERROR_HANDLER(18);
                                    }
                                    if ((a.program_segment_size < count_program) && !("").equals(String.valueOf
                                            (word[i]))) {
                                        throw new ERROR_HANDLER(18);
                                    }
                                    l1 = l1 + 4;
                                }
                                size = size - 4;
                            } else {
                                throw new ERROR_HANDLER(18);
                            }
                        }
                        // loops disk frame vector to get the available page in disk
                        int d = 0;
                        for (int i = 0; i < fmbv_disk.length; i++) {
                            if (fmbv_disk[i] == 0 && program_page > 0) {
                                if (a.program_flag == 0) {
                                    a.segment_index.put("program_segment_pointer", i * 8);
                                    a.program_flag = 1;
                                }
                                fmbv_disk[i] = 1;
                                memoryaddress = i * 8;
                                a.frame_disk.add(memoryaddress);
                                a.disk_page_count++;
                                a.disk_map.put(a.disk_page_count, memoryaddress);
                                program_page--;
                                break;
                            }
                        }
                        //copy the pages from input.txt file to the disk.
                        for (int i = 0; i < word.length && (word[i] != null); i++) {
                            if (word[i].equals("")) {
                                word[i] = "0000";
                                a.disk_fragment_size++;
                            }

                            DISK.disk_fun("write", Integer.toString(memoryaddress), word[i]);
                            memoryaddress++;
                        }


                    }

                    //Copying the input.txt data segment from the job to the disk by checking the available frame
                    input = bufferedReader.readLine();
                    count_inputfile++;
                    if (input.equals("**INPUT")) {
                        String[] word1 = new String[8];
                        for (int k = 0; k < 8; k++) {

                            word1[k] = "";
                        }
                        int word_count = 0;
                        int cc = 0;
                        while (input_words > 0 && (input = bufferedReader.readLine()) != null) {
                            if (cc == 8) {
                                word_count = 0;
                                for (int k = 0; k < 8; k++) {
                                    word1[k] = "";
                                }
                            }
                            count_inputfile++;
                            if (input.equals("**INPUT")) {
                                throw new ERROR_HANDLER(20);
                            }
                            if (input.equals("**FIN")) {
                                throw new ERROR_HANDLER(17);
                            }

                            int joblength = input.length();
                            int count_line = 0;
                            while (joblength > 0) {
                                try {
                                    word1[word_count] = input.substring(count_line, count_line + 4);
                                } catch (Exception e) {
                                    throw new ERROR_HANDLER(17);
                                }
                                joblength = joblength - 4;
                                count_line = count_line + 4;
                                word_count++;
                                input_words--;
                                cc++;

                            }
                            if (word_count == 8 || input_words == 0) {
                                int d = 0;
                                //checking the available disk frame to spool the input.txt data segment
                                for (int i = 0; i < fmbv_disk.length; i++) {
                                    if (fmbv_disk[i] == 0 && input_page > 0) {
                                        if (a.input_flag == 0) {
                                            a.input_flag = 1;
                                            a.segment_index.put("input_segment_pointer", i * 8);
                                        }
                                        fmbv_disk[i] = 1;
                                        memoryaddress = i * 8;
                                        a.frame_disk.add(memoryaddress);
                                        a.disk_page_count++;
                                        a.disk_map.put(a.disk_page_count, memoryaddress);
                                        input_page--;
                                        break;
                                    }
                                }


                                // copy input.txt program segment to disk
                                for (int i = 0; i < 8; i++) {
                                    if (word1[i].equals("")) {

                                        a.disk_fragment_size++;
                                    }
                                    if (!word1[i].equals("")) {
                                        a.input.add(word1[i]);
                                    }
                                    a.input_size++;
                                    DISK.disk_fun("write", Integer.toString(memoryaddress), word1[i]);
                                    memoryaddress++;
                                }

                                for (int k = 0; k < 8; k++) {

                                    word1[k] = "";
                                }
                                word_count = 0;
                            }

                        }

                    } else {
                        throw new ERROR_HANDLER(4);
                    }


                    //allocate space for output program segment

                    while (output_page > 0) {
                        for (int i = 0; i < fmbv_disk.length; i++) {
                            if (fmbv_disk[i] == 0) {
                                if (a.output_flag == 0) {
                                    a.output_flag = 1;
                                    a.segment_index.put("output_segment_pointer", i * 8);
                                }
                                fmbv_disk[i] = 1;
                                memoryaddress = i * 8;
                                a.frame_disk.add(memoryaddress);
                                a.disk_page_count++;
                                a.disk_map.put(a.disk_page_count, memoryaddress);
                                output_page--;
                                break;
                            }

                        }
                        for (int i = 0; i < 8; i++) {
                            a.output_size++;
                            a.disk_fragment_size++;
                            DISK.disk_fun("write", Integer.toString(memoryaddress), "");
                            memoryaddress++;
                        }
                    }

                    input = bufferedReader.readLine();
                    count_inputfile++;
                    if (input.equals("**INPUT")) {
                        throw new ERROR_HANDLER(20);
                    }
                    //end of file
                    if (!("**FIN").equals(input)) {
                        throw new ERROR_HANDLER(5);
                    }

                } else {
                    finale = 1;
                    count_inputfile--;
                    break;

                }

            }
            // It catches all the exceptions thrown by all the components in the Simple Batch System.

            catch (Exception e) {
                tat=(clock+currentjob2.page_fault_time+currentjob2.segment_fault_time)-currentjob2.arrivaltime;
                meter2(currentjob2);
                abnormal_termination++;
                currentjob2.departuretime=clock;
                suspected_infinite=suspected_infinite+currentjob2.run_time-currentjob2.page_fault_time-currentjob2.segment_fault_time-currentjob2.clock1;
                time_abnormal_termination=time_abnormal_termination+currentjob2.run_time;
                available_memory_size = available_memory_size + currentjob2.no_of_frames;

                currentjob2.termtype = "ABNORMAL";
                currentjob2.warningmsg = e.toString();
                currentjob2.exit_flag = 1;
                OUTPUTSPOOLING.output(currentjob2);
                while (true) {
                    count_inputfile++;
                    firstline = bufferedReader.readLine();
                    if (firstline.equals("**FIN") || firstline == null) {
                        break;

                    }
                }
                ready_queue.remove(jid);

            }

        }


    }

}

