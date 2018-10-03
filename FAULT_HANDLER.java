/**
 * The FAULT_HANDLER class consists of two functions Segment_fault_Handler and page_fault_handler which performs segment faults
 * and page faults respectively as the name suggests when necessary.
 */
public class FAULT_HANDLER {

    //handles segment faults usually occurs for first read and write operations
    static void segment_fault_handler(PCB seg, String s) {
        if (s.equals("input.txt")) {
            seg.flag_segmentfault_input = 0;
            seg.segment_fault_time = seg.segment_fault_time + 5;
            seg.run_time = seg.run_time + 5;
            seg.input_data_segment_pointer = seg.pages.get("program_page_size");
        }
        if (s.equals("output")) {
            seg.flag_segmentfault_output = 0;
            seg.segment_fault_time = seg.segment_fault_time + 5;
            seg.run_time = seg.run_time + 5;
            seg.output_data_segment_pointer = (seg.pages.get("program_page_size") + seg.pages.get("input_page_size"));
        }


    }

    //this function handles pagefaults when the required page is not in the memory.
    static String page_fault_handler(int ea, PCB obj) throws ERROR_HANDLER {
        String[] segment_word = new String[8];
        obj.page_fault_time = obj.page_fault_time + 20;
        obj.run_time = obj.run_time + 20;
        obj.page_fault++;
        int fc = obj.frame_count;
        if (fc < obj.no_of_frames) {
            int index = ea * 8, k = 0;
            //getting values from disk starting from page index
            for (int i = index; i < index + 8; i++) {
                segment_word[k] = SYSTEM.disk[i];
                k++;
            }
            k = 0;
            // pushing values to memory into index retrieved from pcb arraylist frame using frame_count
            int mem_index = obj.frame.get(obj.frame_count);
            for (int i = mem_index; i < mem_index + 8; i++) {
                SYSTEM.memory_variable[i] = Hextoany.hexToBinary(segment_word[k]);
                k++;
            }

            obj.pmt[obj.current_page].page_number = Integer.toString(obj.current_page);
            obj.pmt[obj.current_page].frame_number = Integer.toString(mem_index / 8);
            obj.pmt[obj.current_page].valid_invalid = "1";
            obj.frame_count++;

        } else {
//page fault replacement algorithm-Enhanced second chance
            int[] page_replace = new int[obj.frame.size()];
            int flag = 0, flag1 = 0;

            //placing all the indexes of the pages that are present in the allocated memory frames.
            for (int i = 0; i < page_replace.length; i++) {

                for (int j = 0; j < obj.total_pages; j++) {

                    if (obj.pmt[j].frame_number.equals(Integer.toString(obj.frame.get(i) / 8))) {

                        page_replace[i] = Integer.parseInt(obj.pmt[j].page_number);
                        break;
                    }
                }
            }


            while (flag1 == 0) {

                for (int i = obj.faulty_pointer, count = 0; i < obj.frame.size() && count != obj.frame.size(); i++, count++) {
                    //Cycle through the buffer looking for <reference_bit=0, dirty_bit=0>. If one is found, use that page.
                    if (obj.pmt[page_replace[i]].reference_bit.equals("0") && obj.pmt[page_replace[i]].dirty_bit.equals("0")) {
                        obj.pmt[page_replace[i]].valid_invalid = "0";
                        obj.pmt[page_replace[i]].frame_number = "";
                        int index = ea * 8, k = 0;
                        //getting values from disk starting from page index
                        for (int p = index; p < index + 8; p++) {
                            segment_word[k] = SYSTEM.disk[p];
                            k++;
                        }
                        k = 0;
                        int mem_index = obj.frame.get(i);
                        for (int q = mem_index; q < mem_index + 8; q++) {
                            if (segment_word[k].equals("")|| segment_word[k]==null) {
                                SYSTEM.memory_variable[q] = "";
                            } else {
                                SYSTEM.memory_variable[q] = Hextoany.hexToBinary(segment_word[k]);
                            }

                            k++;
                        }

                        obj.pmt[obj.current_page].page_number = Integer.toString(obj.current_page);
                        obj.pmt[obj.current_page].frame_number = Integer.toString(mem_index / 8);
                        obj.pmt[obj.current_page].valid_invalid = "1";
                        obj.pmt[obj.current_page].reference_bit = "0";
                        obj.pmt[obj.current_page].dirty_bit = "0";
                        flag = 1;
                        flag1 = 1;
                        obj.faulty_pointer = i;
                        break;
                    }
                    if (i == obj.frame.size() - 1) {
                        i = -1;
                    }

                }

                if (flag == 0) {
                    // Cycle through the buffer looking for <reference_bit=0, dirty_bit=1>. Set the use bit to zero
                    // for all frames bypassed.
                    for (int i = obj.faulty_pointer, count = 0; i < obj.frame.size() && count != obj.frame.size(); i++, count++) {

                        if (obj.pmt[page_replace[i]].reference_bit.equals("0") && obj.pmt[page_replace[i]].dirty_bit.equals("1")) {
                            obj.pmt[page_replace[i]].valid_invalid = "0";
                            obj.pmt[page_replace[i]].frame_number = "";
                            obj.pmt[page_replace[i]].dirty_bit = "0";
                            int index = ea * 8, k = 0;
                            //getting values from disk starting from page index
                            for (int p = index; p < index + 8; p++) {
                                segment_word[k] = SYSTEM.disk[p];
                                k++;
                            }
                            //writing back to disk as dirty bit is 1
                            int mem_index = obj.frame.get(i);
                            k = mem_index;
                            int disk_index = obj.disk_map.get(Integer.parseInt(obj.pmt[page_replace[i]].page_number)) ;
                            for (int q = disk_index; q < disk_index + 8; q++) {
                                String s;
                                if (SYSTEM.memory_variable[k].equals("")) {
                                    s = "";
                                } else {
                                    s = Hextoany.Binarytohex(SYSTEM.memory_variable[k]);
                                }
                                int len = s.length();

                                StringBuffer sb = new StringBuffer(s);
                                sb.reverse();
                                for (int u = 0; u < 4 - len; u++) {
                                    sb.append('0');
                                }
                                s = sb.reverse().toString();
                                SYSTEM.disk[q] = s;

                                k++;
                            }
                            k = 0;
                            //writing the required page to the respective page
                            for (int q = mem_index; q < mem_index + 8; q++) {
                                if (segment_word[k].equals("")) {
                                    SYSTEM.memory_variable[q] = "";
                                } else {
                                    SYSTEM.memory_variable[q] = Hextoany.hexToBinary(segment_word[k]);
                                }
                                k++;
                            }
                            obj.pmt[obj.current_page].page_number = Integer.toString(obj.current_page);
                            obj.pmt[obj.current_page].frame_number = Integer.toString(mem_index / 8);
                            obj.pmt[obj.current_page].valid_invalid = "1";
                            obj.pmt[obj.current_page].reference_bit = "0";
                            obj.pmt[obj.current_page].dirty_bit = "0";

                            flag = 1;
                            flag1 = 1;
                            obj.faulty_pointer = i;
                            break;
                        } else {

                            obj.pmt[page_replace[i]].reference_bit = "0";

                        }
                        if (i == obj.frame.size() - 1) {
                            i = -1;
                        }
                    }

                }
                if (obj.faulty_pointer == obj.frame.size()) {
                    obj.faulty_pointer = 0;

                }


            }


        }

        return "";
    }
}
