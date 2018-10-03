/**
 * The PCB(ProcessControlBlock) class consists of variables that are unique to every job. These variables describe the
 * state and characteristic of the respective job.
 */
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;


public class PCB {
     int input_flag = 0, output_flag = 0, program_flag = 0, in_flag=0;
    String[] stack = new String[8];
     int no_of_frames=0,pc,pc_old,ea = 0;
    String stack1;
    int job_id, tos = 0, clock1=0, virtualclock = 0, time = 0, instriction_type, overflow_flag;
    //stores the current objpc of a job
    String br,ir,pc_cpu;
    String  trace_flag;
    BigInteger memory_util_word_num, memory_util_word_den;
    double percentage;
    String termtype, warningmsg, termmsg, blocked_status;
    //input.txt,output,program segment sizes of a job
    int input_segment_size, output_segment_size, memory_utilization,program_segment_size, total_pages, blocked_time, loader_flag=0, disk_page_count=-1;
    //points to the program PMT
    int program_segment_pointer, executiontime, no_pagefault=0, no_segmentfault=0,given_id;
     HashMap<Integer,Integer > disk_map= new HashMap<>();
    PrintWriter pw;
    //points to input.txt PMT
    int input_data_segment_pointer, internal_jid, arrivaltime, departuretime;
    int input_data_segment_index=0;
    int run_time=0, execution_time_in_cpu, execution_flag=0, current_page, input_size=0, output_size=0,cpushot=0,ioshot=0, infinite_time=0;
    //points to output PMT
    int output_data_segment_pointer, error_flag_input=0, job_error=0;
    //count on no of available frames used
    int frame_count = 0;
    //count on pagefaults for respective job
    int page_fault=0,pmt_index,totpage;
    int page_fault_time=0;
    int segment_fault_time=0;
    //Flag for input.txt segment fault
    int flag_segmentfault_input=0,pcc;
    //Flag for output segment fault
    int flag_segmentfault_output=0;
    int ea_old;
    int flag_input=0;
    int flag_output=0;
    int flag_pagefault=0;
    int exit_flag=0;
    ArrayList<String> map = new ArrayList<>();
    //stores all inputs
    ArrayList<String> input=new ArrayList<>();
    //stores all outputs
    ArrayList<String> output=new ArrayList<>();
    int clock=0;
    //page fault pointer for the particular job
    int faulty_pointer = 0,halt_flag=0;
    int disk_fragment_size=0;
    int memory_fragment_size=0;
    int output_data_segment_index=0;
    String read;
    String write;
    //this arraylist called frame holds the base address of the frames assigned to a job
    ArrayList<Integer> frame = new ArrayList<Integer>(6);
    ArrayList<Integer> frame_disk = new ArrayList<Integer>();
    //Holds No of Pages for each segment
    HashMap<String, Integer> pages = new HashMap<>();
    //Each segment index in disk
    HashMap<String, Integer> segment_index = new HashMap<>(3);
    //pmt objects which are used by smt
    PMT[] pmt;

    public void pcb_fun() {
        pmt = new PMT[total_pages];
        for (int i = 0; i < pmt.length; i++)
            pmt[i] = new PMT();
    }
}
