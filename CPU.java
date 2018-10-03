/**
 * After LOADER completes its execution the SYSTEM transfers its control to the CPU.
 * CPU gets two parameters, objpc and a trace_flag.
 * objpc provides CPU with the start address from where the execution should start.
 * And if the trace_flag is enabled the CPU prints objpc,br,ea,(ea),objtos,objstack[objtos] for every instruction it executes.
 */
import java.util.ArrayList;
import java.util.ListIterator;


class CPU {

    @SuppressWarnings("unchecked")
    public static PCB cpu_fun(String x, int y, PCB obj) throws ERROR_HANDLER {
        String opcode, operation;
        obj.cpushot++;
        //objpc value is calculated by adding br to it.
        obj.pc = Integer.parseInt(x);

        while (true) {

            obj.pc_old = (obj.pc);
            //If clock exceeds 10000 it throws a warning and the exception is handled by the EXCEPTION_HANDLER
            //  if (SYSTEM.clock > 10000) {
            //      throw new ERROR_HANDLER(11);
            //  }
            //The below condition checks the overflow and underflow condition of top of objstack and throws a respective
            // exception if condition file.
            if (obj.tos < 0) {
                throw new ERROR_HANDLER(12);
            } else if (obj.tos > 7) {
                throw new ERROR_HANDLER(13);
            }
            ArrayList al1 = new ArrayList();
            opcode = "";
            if(SYSTEM.snapshot>200){
                SYSTEM.snapshot=0;
                OUTPUTSPOOLING.snapshot_fun(obj);

            }
            if(obj.run_time>10000){
                SYSTEM.infinite_id.add(SYSTEM.job_id.get(obj));
                throw new ERROR_HANDLER(11);

            }
            if (obj.execution_time_in_cpu >= 20) {
                obj.execution_flag = 1;
                break;
            }
            SYSTEM.clock++;
            SYSTEM.snapshot++;
            // Each Instruction is read from the MEMORY using objpc.
            obj.ir = MEMORY.Memory_function("read", Integer.toBinaryString(obj.pc), "0", obj);

            if (obj.ir.equals("pagefault")) {

                break;
            }
            // An arraylist al1 is created to store respective variable values to print
            if (Integer.parseInt(obj.trace_flag) == 1) {
                al1.add(Integer.toHexString(obj.pc));
                al1.add((obj.br));
                al1.add(Hextoany.Binarytohex(obj.ir));
                al1.add(Integer.toHexString(obj.tos));
                if (obj.stack[obj.tos] == null || obj.stack[obj.tos].equals("")) {
                    al1.add(Hextoany.Binarytohex("0"));
                } else {
                    al1.add(Hextoany.Binarytohex(obj.stack[obj.tos]));
                }
            }
            obj.pcc = obj.pc = obj.pc + 1;
            // Checks whether an instruction is zero
            if (obj.ir.charAt(0) == '0') {

                if (Integer.parseInt(obj.trace_flag) == 1) {
                    al1.add(Integer.toHexString(obj.ea));
                    al1.add(0);
                }
                // The SYSTEM clock is incremented for each fetch of an instruction
                obj.run_time++;
                SYSTEM.clock++;
                SYSTEM.snapshot++;
                obj.execution_time_in_cpu++;
                obj.instriction_type = 1;
                opcode = obj.ir.substring(3, obj.ir.length() / 2);
                operation = zeroaddress(opcode, obj);
                if (obj.flag_segmentfault_input == 1 || obj.flag_segmentfault_output == 1 || obj.flag_pagefault == 1) {
                    break;
                }
                // If halt return the control to the SYSTEM
                if (operation.equals("HALT")) {
                    if(obj.trace_flag.equals("1")){
                    ListIterator<String> litr = al1.listIterator();
                    while (litr.hasNext()) {

                        {
                            obj.pw.print(String.format("%7s", litr.next()));
                        }


                    }}
                    obj.exit_flag = 1;
                    if(obj.trace_flag.equals("1")) {
                        obj.pw.close();
                    }
                    obj.halt_flag=1;
                    OUTPUTSPOOLING.output(obj);

                    break;
                }
                // Increment the clock by 15 if an I/O instruction is found.
                if (operation.equals("RD") || operation.equals("WR")) {
                    SYSTEM.clock = SYSTEM.clock + 15;
                    SYSTEM.snapshot=SYSTEM.snapshot+15;
                    obj.execution_time_in_cpu = obj.execution_time_in_cpu + 15;
                    obj.run_time = obj.run_time + 15;
                    obj.clock1 = obj.clock1 + 15;
                }
                opcode = "";
                if (obj.pcc == obj.pc) {
                    obj.run_time++;
                    SYSTEM.clock++;
                    SYSTEM.snapshot++;
                    obj.execution_time_in_cpu++;
                    opcode = obj.ir.substring(11, obj.ir.length());
                    operation = zeroaddress(opcode, obj);
                    if (obj.flag_segmentfault_input == 1 || obj.flag_segmentfault_output == 1 || obj.flag_pagefault ==
                            1) {
                        break;
                    }
                    if (operation.equals("HALT")) {
                        obj.halt_flag=1;
                        if(obj.trace_flag.equals("1")){
                        ListIterator<String> litr = al1.listIterator();
                        while (litr.hasNext()) {

                            {
                                obj.pw.print(String.format("%7s", litr.next()));
                            }


                        }}
                        obj.exit_flag = 1;
                        if(obj.trace_flag.equals("1")){
                        obj.pw.close();}
                        OUTPUTSPOOLING.output(obj);

                        break;
                    }
                    if (operation.equals("RD") || operation.equals("WR")) {
                        SYSTEM.clock = SYSTEM.clock + 15;
                        SYSTEM.snapshot=SYSTEM.snapshot+15;
                        obj.execution_time_in_cpu = obj.execution_time_in_cpu + 15;
                        obj.run_time = obj.run_time + 15;
                        obj.clock1 = obj.clock1 + 15;
                    }
                    // PRINT TO FILE
                }
                if (Integer.parseInt(obj.trace_flag) == 1) {
                    al1.add(Integer.toHexString(obj.tos));
                    if (obj.stack[obj.tos] == null || obj.stack[obj.tos].equals("")) {
                        al1.add(Hextoany.Binarytohex("0"));
                    } else {
                        al1.add(Hextoany.Binarytohex(obj.stack[obj.tos]));
                    }
                    al1.add(Integer.toHexString(obj.ea));
                    if (SYSTEM.memory_variable[obj.ea] == null || SYSTEM.memory_variable[obj.ea].equals("") || SYSTEM.memory_variable[obj.ea].equals(" ")) {
                        al1.add(Hextoany.Binarytohex("0"));
                    } else {
                        al1.add(Hextoany.Binarytohex(SYSTEM.memory_variable[obj.ea]));
                    }
                }

            }
            // Executes else condition if it is one address Instruction.
            else {
                if (Integer.parseInt(obj.trace_flag) == 1) {
                    al1.add(Integer.toHexString(obj.ea));
                    if (SYSTEM.memory_variable[obj.ea] == null || SYSTEM.memory_variable[obj.ea].equals("")|| SYSTEM.memory_variable[obj.ea].equals(" ")) {
                        al1.add(Hextoany.Binarytohex("0"));
                    } else {
                        al1.add(Hextoany.Binarytohex(SYSTEM.memory_variable[obj.ea]));
                    }

                }
                opcode = obj.ir.substring(1, 6);
                obj.instriction_type = 2;
                operation = oneaddress(opcode, obj);
                if (obj.flag_pagefault == 1) {
                    break;
                }
                if (Integer.parseInt(obj.trace_flag) == 1) {
                    al1.add(Integer.toHexString(obj.tos));
                    al1.add((Hextoany.Binarytohex(obj.stack[obj.tos])));
                    al1.add(Integer.toHexString(obj.ea));
                    if (SYSTEM.memory_variable[obj.ea] == null || SYSTEM.memory_variable[obj.ea].equals("")|| SYSTEM.memory_variable[obj.ea].equals(" ")) {
                        al1.add(Hextoany.Binarytohex("0"));
                    } else {
                        al1.add(Hextoany.Binarytohex(SYSTEM.memory_variable[obj.ea]));
                    }

                }


                if (operation.equals("HALT")) {
                    obj.halt_flag=1;
                    if(obj.trace_flag.equals("1")) {
                        obj.pw.close();
                    }
                    OUTPUTSPOOLING.output(obj);

                    break;
                }
                if (operation.equals("RD") || operation.equals("WR")) {
                    SYSTEM.clock = SYSTEM.clock + 15;
                    SYSTEM.snapshot=SYSTEM.snapshot+15;
                    obj.execution_time_in_cpu = obj.execution_time_in_cpu + 15;
                    obj.run_time = obj.run_time + 15;
                    obj.clock1 = obj.clock1 + 15;
                } else {
                    SYSTEM.clock = SYSTEM.clock + 4;
                    SYSTEM.snapshot=SYSTEM.snapshot+4;
                    obj.execution_time_in_cpu = obj.execution_time_in_cpu + 4;
                    obj.run_time = obj.run_time + 4;
                }
            }

            if(obj.trace_flag.equals("1")){
            ListIterator<String> litr = al1.listIterator();
            while (litr.hasNext()) {
                obj.pw.print(String.format("%7s", litr.next()));

            }
            obj.pw.println();
        }}
        return obj;
    }

    // Function for executing a Zero address instruction.
    public static String zeroaddress(String ins, PCB obj) throws ERROR_HANDLER {
        String d = "";
        int i;
        switch (ins) {
            case "00000": {
                return "NOP";

            }
            case "00001": {
                String len;
                //performs a binary 'OR' operation on top two objstack elements
                len = Integer.toBinaryString(Hextoany.bintodecimal(obj.stack[obj.tos]) | Hextoany.bintodecimal(obj.stack[obj.tos - 1]));
                d = cut(d);
                obj.stack[obj.tos - 1] = len;
                obj.tos = obj.tos - 1;
                return "OR";
            }
            case "00010": {
                //performs a binary 'AND' operation on top two objstack elements
                d = Integer.toBinaryString(Hextoany.bintodecimal(obj.stack[obj.tos]) & Hextoany.bintodecimal(obj.stack[obj.tos - 1]));
                d = cut(d);
                obj.stack[obj.tos - 1] = d;
                obj.tos = obj.tos - 1;
                return "AND";
            }
            case "00011": {
                //performs a binary 'NOT' operation on top two objstack elements
                String s = Integer.toBinaryString(~Hextoany.bintodecimal((obj.stack[obj.tos])));
                StringBuffer sb = new StringBuffer(s);
                obj.stack[obj.tos] = sb.delete(0, 16).toString();

                return "NOT";
            }
            case "00100": {
                //performs a binary 'XOR' operation on top two objstack elements
                d = Integer.toBinaryString(Hextoany.bintodecimal(obj.stack[obj.tos]) ^ Hextoany.bintodecimal(obj.stack[obj.tos - 1]));
                d = cut(d);
                obj.stack[obj.tos - 1] = d;
                obj.tos = obj.tos - 1;
                return "XOR";
            }
            case "00101": {
                //neg function is called which checks whether the objtos contains a negative element.
                i = neg(obj);
                //performs a binary 'ADD' operation on top two objstack elements
                d = Integer.toBinaryString(Hextoany.bintodecimal(obj.stack[obj.tos - 1]) + i);
                d = cut(d);
                obj.stack[obj.tos - 1] = d;
                obj.tos = obj.tos - 1;
                return "ADD";
            }
            case "00110": {
                i = neg(obj);
                //performs a binary 'SUB' operation on top two objstack elements
                d = Integer.toBinaryString(Hextoany.bintodecimal(obj.stack[obj.tos - 1]) - i);
                d = cut(d);
                obj.stack[obj.tos - 1] = d;
                obj.tos = obj.tos - 1;
                return "SUB";
            }
            case "00111": {
                i = neg(obj);
                //performs a binary 'MUL' operation on top two objstack elements
                d = Integer.toBinaryString(i * Hextoany.bintodecimal(obj.stack[obj.tos - 1]));
                d = cut(d);
                obj.stack[obj.tos - 1] = d;
                obj.tos = obj.tos - 1;
                return "MUL";
            }
            case "01000": {
                i = neg(obj);
                //performs a binary 'DIV' operation on top two objstack elements
                try {
                    d = Integer.toBinaryString(Hextoany.bintodecimal(obj.stack[obj.tos - 1]) / i);
                }catch (Exception e){ throw  new ERROR_HANDLER(16);}
                d = cut(d);
                obj.stack[obj.tos - 1] = d;
                obj.tos = obj.tos - 1;
                return "DIV";
            }
            case "01001": {
                i = neg(obj);
                //performs a binary 'MOD' operation on top two objstack elements
             try{
                d = Integer.toBinaryString(Hextoany.bintodecimal(obj.stack[obj.tos - 1]) % i);
            }catch (Exception e){

                throw new ERROR_HANDLER(13);
            }
                d = cut(d);
                obj.stack[obj.tos - 1] = d;
                obj.tos = obj.tos - 1;
                return "MOD";
            }
            case "01010": {
                //performs left shift operation on objtos.
                d = Integer.toBinaryString(Hextoany.bintodecimal(obj.stack[obj.tos]) << 1);
                d = cut(d);
                obj.stack[obj.tos] = d;
                return "SL";
            }
            case "01011": {
                //performs right shift operation on objtos.
                d = Integer.toBinaryString(Hextoany.bintodecimal(obj.stack[obj.tos]) >> 1);
                d = cut(d);
                obj.stack[obj.tos] = d;
                return "SR";
            }
            case "01100": {
                i = neg(obj);
                // compares whether objstack[objtos-1] is greater than objstack[objtos]
                boolean len = (Hextoany.bintodecimal(obj.stack[obj.tos - 1]) > i);
                if (len) {
                    obj.stack[obj.tos + 1] = "0000000000000001";
                } else {
                    obj.stack[obj.tos + 1] = "0000000000000000";
                }
                obj.tos = obj.tos + 1;
                return "CPG";
            }
            case "01101": {
                i = neg(obj);
                // compares whether objstack[objtos-1] is less than objstack[objtos]
                boolean len = (Hextoany.bintodecimal(obj.stack[obj.tos - 1]) < i);
                if (len) {
                    obj.stack[obj.tos + 1] = "0000000000000001";
                } else {
                    obj.stack[obj.tos + 1] = "0000000000000000";
                }
                obj.tos = obj.tos + 1;
                return "CPL";
            }
            case "01110": {
                i = neg(obj);
                // compares whether objstack[objtos-1] is equal than objstack[objtos]
                boolean len = (Hextoany.bintodecimal(obj.stack[obj.tos - 1]) == i);
                if (len) {
                    obj.stack[obj.tos + 1] = "0000000000000001";
                } else {
                    obj.stack[obj.tos + 1] = "0000000000000000";
                }
                obj.tos = obj.tos + 1;
                return "CPE";
            }
            case "01111": {
                return "BR";
            }
            case "10000": {
                return "BRT";
            }
            case "10001": {
                return "BRF";
            }
            case "10010": {

                return "CALL";
            }
            case "10011": {
                obj.ioshot++;
//segment fault handler for first read operation
                if (obj.input_data_segment_pointer == 0) {

                    obj.flag_segmentfault_input = 1;

                    return "";

                } else {


                    if (obj.input_data_segment_index + 1 > obj.input_segment_size) {
                        throw new ERROR_HANDLER(21);
                    }
                    int s = (obj.input_data_segment_pointer) * 8 + (obj.input_data_segment_index);
//page fault occurs if the respective page is not in the memory - job releases CPU and control is returned to the SYSTEM
                    d = (MEMORY.Memory_function("read", Integer.toBinaryString(s), "0", obj));
                    //if pagefault occurs job releases CPU and returns the control to the SYSTEM
                    if (d.equals("pagefault")) {
                        return "";
                    }
                    obj.input_data_segment_index++;

                    obj.stack[++obj.tos] = d;
                    return "RD";
                }
            }
            case "10100": {
                obj.ioshot++;
                //segment fault handler for first write operation - job releases CPU and control is returned to the SYSTEM
                if (obj.output_data_segment_pointer == 0) {
                    obj.flag_segmentfault_output = 1;

                    return "";

                } else {
                    if (obj.output_data_segment_index + 1 > obj.output_segment_size) {
                        throw new ERROR_HANDLER(22);
                    }
                    int s = (obj.output_data_segment_pointer) * 8 + (obj.output_data_segment_index);

                    //if pagefault occurs job releases CPU and returns the control to the SYSTEM
                    String wri = MEMORY.Memory_function("write", Integer.toString(s), obj.stack[obj.tos], obj);
                    if (wri.equals("pagefault")) {
                        return "";
                    }
                    obj.output_data_segment_index++;
                    obj.output.add(obj.stack[obj.tos]);


                    obj.tos = obj.tos - 1;
                    obj.disk_fragment_size--;
                    return "WR";
                }
            }
            case "10101": {
                // contents of objstack[objtos] is placed in objpc
                obj.pc = (Hextoany.bintodecimal(obj.stack[obj.tos]));
                obj.tos = obj.tos - 1;
                return "RTN";
            }
            case "10110": {
                return "PUSH";
            }
            case "10111": {
                return "POP";
            }
            case "11000": {
                return "HALT";
            }
            default:
                break;
        }

        return "";
    }

    // Function for executing a One address instruction.
    public static String oneaddress(String ins, PCB obj) throws ERROR_HANDLER {
        String d, len1;
        int i;

        // Condition to evaluate the effective address
        if (obj.ir.charAt(6) == '1') {
            obj.ea = Hextoany.bintodecimal(obj.ir.substring(9, obj.ir.length())) + Hextoany.bintodecimal(obj.stack[obj.tos]) +
                    Integer.parseInt(obj.br);

        } else {
            obj.ea = Hextoany.bintodecimal(obj.ir.substring(9, obj.ir.length())) + Integer.parseInt(obj.br);

        }
        switch (ins) {
            case "00000": {
                return "NOP";
            }
            case "00001": {
                // performs "OR" on objstack[objtos] and element present in effective address.
                len1 = index(obj);
                if (len1.equals("pagefault")) {
                    return "";
                }
                d = Integer.toBinaryString(Hextoany.bintodecimal(obj.stack[obj.tos]) | Hextoany.bintodecimal(len1));
                d = cut(d);
                obj.stack[obj.tos] = d;
                return "OR";
            }
            case "00010": {

                len1 = index(obj);
                if (len1.equals("pagefault")) {
                    return "";
                }
                // performs "AND" on objstack[objtos] and element present in effective address.
                d = Integer.toBinaryString(Hextoany.bintodecimal(obj.stack[obj.tos]) & Hextoany.bintodecimal(len1));
                d = cut(d);

                obj.stack[obj.tos] = d;
                return "AND";
            }
            case "00011": {
                return "NOT";
            }
            case "00100": {
                len1 = index(obj);
                if (len1.equals("pagefault")) {
                    return "";
                }
                // performs "XOR" on objstack[objtos] and element present in effective address.
                d = Integer.toBinaryString(Hextoany.bintodecimal(obj.stack[obj.tos]) ^ Hextoany.bintodecimal(len1));
                d = cut(d);
                obj.stack[obj.tos] = d;
                return "XOR";
            }
            case "00101": {
                len1 = index(obj);
                if (len1.equals("pagefault")) {
                    return "";
                }
                // performs "ADD" on objstack[objtos] and element present in effective address.

                if (obj.stack[obj.tos].charAt(0) == '1') {
                    obj.stack1 = reverse(obj.stack[obj.tos]);
                    i = 0 - Hextoany.bintodecimal(obj.stack1);
                } else {
                    i = Hextoany.bintodecimal(obj.stack[obj.tos]);
                }
                d = Integer.toBinaryString(i + Hextoany.bintodecimal(len1));
                d = cut(d);
                obj.stack[obj.tos] = d;
                return "ADD";
            }
            case "00110": {

                len1 = index(obj);
                if (len1.equals("pagefault")) {
                    return "";
                }
                // performs "SUB" on objstack[objtos] and element present in effective address.
                i = neg(obj);
                d = Integer.toBinaryString(i - Hextoany.bintodecimal(len1));
                d = cut(d);
                obj.stack[obj.tos] = d;
                return "SUB";
            }
            case "00111": {
                // performs "MUL" on objstack[objtos] and element present in effective address.
                len1 = index(obj);
                if (len1.equals("pagefault")) {
                    return "";
                }
                i = neg(obj);
                d = Integer.toBinaryString(i * Hextoany.bintodecimal(len1));
                d = cut(d);
                obj.stack[obj.tos] = d;
                return "MUL";
            }
            case "01000": {
                // performs "DIV" on objstack[objtos] and element present in effective address.
                len1 = index(obj);
                if (len1.equals("pagefault")) {
                    return "";
                }
                i = neg(obj);
                try {
                    d = Integer.toBinaryString(i / Hextoany.bintodecimal(len1));
                }catch (Exception e){

                    throw new ERROR_HANDLER(13);
                }
                d = cut(d);
                obj.stack[obj.tos] = d;
                return "DIV";
            }
            case "01001": {
                // performs "MOD" on objstack[objtos] and element present in effective address.
                len1 = index(obj);
                if (len1.equals("pagefault")) {
                    return "";
                }
                i = neg(obj);
                try{
                d = Integer.toBinaryString(i % Hextoany.bintodecimal(len1));
            }catch (Exception e){

                throw new ERROR_HANDLER(13);
            }
                d = cut(d);
                obj.stack[obj.tos] = d;
                return "MOD";
            }
            case "01010": {

                return "SL";
            }
            case "01011": {

                return "SR";
            }
            case "01100": {
                len1 = index(obj);
                if (len1.equals("pagefault")) {
                    return "";
                }
                i = neg(obj);
                // compares the objstack[objtos] element with the element present in effective address
                // and places true if objstack[objtos] is greater than (ea)
                boolean len = (i > Hextoany.bintodecimal(len1));
                if (len) {
                    obj.stack[obj.tos + 1] = "0000000000000001";
                } else {
                    obj.stack[obj.tos + 1] = "0000000000000000";
                }
                obj.tos = obj.tos + 1;
                return "CPG";
            }
            case "01101": {
                len1 = index(obj);
                if (len1.equals("pagefault")) {
                    return "";
                }
                i = neg(obj);
                // compares the objstack[objtos] element with the element present in effective address
                // and places true if objstack[objtos] is less than (ea)
                boolean len = (i < Hextoany.bintodecimal(len1));
                if (len) {
                    obj.stack[obj.tos + 1] = "0000000000000001";
                } else {
                    obj.stack[obj.tos + 1] = "0000000000000000";
                }
                obj.tos = obj.tos + 1;
                return "CPL";
            }
            case "01110": {
                len1 = index(obj);
                if (len1.equals("pagefault")) {
                    return "";
                }
                i = neg(obj);
                // compares the objstack[objtos] element with the element present in effective address
                // and places true if objstack[objtos] is equal than (ea)
                boolean len = (i == Hextoany.bintodecimal(len1));
                if (len) {
                    obj.stack[obj.tos + 1] = "0000000000000001";
                } else {
                    obj.stack[obj.tos + 1] = "0000000000000000";
                }
                obj.tos = obj.tos + 1;
                return "CPE";
            }
            case "01111": {

                obj.pc = obj.ea;
                return "BR";
            }
            case "10000": {
                // brances to effective address of memory if objstack[objtos]is true
                if (obj.stack[obj.tos].equals("0000000000000001")) {

                    obj.pc = obj.ea;
                }
                obj.tos = obj.tos - 1;
                return "BRT";
            }
            case "10001": {
                // brances to effective address of memory if objstack[objtos]is false
                if (obj.stack[obj.tos].equals("0000000000000000")) {

                    obj.pc = obj.ea;
                }
                obj.tos = obj.tos - 1;
                return "BRF";
            }
            case "10010": {

                d = Integer.toBinaryString(obj.pc);
                d = cut(d);
                obj.stack[++obj.tos] = d;
                obj.pc = obj.ea;
                return "CALL";
            }
            case "10011": {

                return "RD";
            }
            case "10100": {

                return "WR";
            }
            case "10101": {

                return "RTN";
            }
            case "10110": {
                // push the element present in effective address to objstack[objtos]
                len1 = index(obj);
                if (len1.equals("pagefault")) {
                    return "";
                }
                try{
                obj.stack[++obj.tos] = len1;
            }catch(Exception e){
                throw new ERROR_HANDLER(13);

            }
                return "PUSH";
            }
            case "10111": {

                // pops element in objstack[objtos] to effective address of memory
                if (obj.ir.charAt(6) == '1') {
                    String s = MEMORY.Memory_function("write", Integer.toString(Hextoany.bintodecimal
                            (obj.ir.substring(9, obj.ir.length())) + Hextoany.bintodecimal(obj.stack[obj.tos])), obj.stack[obj.tos], obj);
                    if (s.equals("pagefault")) {
                        return "";
                    }
                } else {

                    String s = MEMORY.Memory_function("write", Integer.toString(Hextoany.bintodecimal
                            (obj.ir.substring(9, obj.ir.length()))), obj.stack[obj.tos], obj);
                    if (s.equals("pagefault")) {
                        return "";
                    }
                }
                obj.tos = obj.tos - 1;
                return "POP";
            }
            case "11000": {
                System.exit(1);
                return "HLT";
            }
            default:
                break;
        }


        return "";
  }

    // complements an input.txt binary parameter
    private static String reverse(String d) {

        String e = Integer.toBinaryString(0 - Hextoany.bintodecimal(d));
        if (e.length() < 16) {
            e = Hextoany.pad(e);
        } else if (e.length() > 16) {
            e = trim(e);
        }
        return e;
    }

    private static String cut(String d) {
        if (d.length() < 16) {
            d = Hextoany.pad(d);
        } else if (d.length() > 16) {
            d = trim(d);
        }
        return d;
    }

    private static int neg(PCB obj) {
        int i;
        if (obj.stack[obj.tos].charAt(0) == '1') {
            obj.stack1 = reverse(obj.stack[obj.tos]);
            i = 0 - Hextoany.bintodecimal(obj.stack1);
        } else {
            i = Hextoany.bintodecimal(obj.stack[obj.tos]);
        }
        return i;
    }

    // calculates effective address
    static String index(PCB obj) throws ERROR_HANDLER {
        String len1;
        String g = Integer.toBinaryString(Hextoany.bintodecimal(obj.ir.substring(9, obj.ir.length())) +
                Integer.parseInt(obj.br));
        String g1 = cut(g);
        if (obj.ir.charAt(6) == '1') {

            len1 = MEMORY.Memory_function("read", g1, obj.stack[obj.tos], obj);
        } else {
            len1 = MEMORY.Memory_function("read", g1, "0", obj);

        }
        return len1;
    }



    // trims an instruction if it has more than 16 bits
    static String trim(String ins) {
        String len;
        StringBuilder sb = new StringBuilder(ins);
        sb.reverse();
        String sb1 = sb.substring(0, 16);
        StringBuilder sb2 = new StringBuilder(sb1);
        len = sb2.reverse().toString();
        return len;
    }

    //outputs te final result


}
