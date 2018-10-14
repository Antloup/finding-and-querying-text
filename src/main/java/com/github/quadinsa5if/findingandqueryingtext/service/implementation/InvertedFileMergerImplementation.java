package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.model.ReversedIndexIdentifier;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileMerger;
import com.github.quadinsa5if.findingandqueryingtext.tokenizer.FileIterator;
import com.sun.deploy.util.ArrayUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class InvertedFileMergerImplementation implements InvertedFileMerger {
    private List<File> parts;
    private List<FileIterator> iterators_pool;
    private List<ReversedIndexIdentifier> triplets;
    private List<File> transient_headers_files;
    private BufferedWriter bw;
    private int previous_offset=0;
    private int previous_total_length=0;
    private final char DELIMITER = '/';

    public InvertedFileMergerImplementation(List<File> transient_headers_files){
        this.initiate(transient_headers_files);
    }

    private void initiate(List<File> transient_headers_files){
        //Initiate
        for (File file : this.transient_headers_files){
            try {
                FileIterator iter = new FileIterator(file,DELIMITER);
                this.triplets.add(this.triplet_builder(iter.next()));
                this.iterators_pool.add(iter);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Iterators list initiation failed");
            }
        }
        this.previous_offset=0;
        this.previous_total_length=0;
        try {
            File header_file =new File("merged_headers.txt");
            if(!header_file.exists()){
                header_file.createNewFile();
            }
            this.bw = new BufferedWriter(new FileWriter(header_file, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public File merge(List<File> parts){
        /*for(File part : parts){
        }*/
        File full_file;
        return null;
    }


    //Take a set of sorted headers and merge
    public void header_builder(List<File> transient_headers_files) throws Exception {
        //create new header file
        List<Integer> mins_indexes = this.get_mins_indexes(this.triplets);
        List<ReversedIndexIdentifier> mins = this.get_mins(mins_indexes,this.triplets);

        try {
            ReversedIndexIdentifier new_val;
            new_val = this.get_new_triplet(mins,this.previous_offset,this.previous_total_length);


            String new_line = String.format("%s:%d:%d/",new_val.term,new_val.offset,new_val.length);
            this.bw.write(new_line);
            System.out.println("New line has been written into merged_header.txt");

            this.previous_offset=new_val.offset;
            this.previous_total_length=new_val.length;

            for (int i = 0; i<=iterators_pool.size(); i++){
                if(mins_indexes.contains(i)){
                    FileIterator iterator = this.iterators_pool.get(i);
                    String s = iterator.next();
                    ReversedIndexIdentifier v = this.triplet_builder(s);
                    this.triplets.set(i,v);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.bw.close();
            throw new Exception("Error in header building");
        }
    }



    public ReversedIndexIdentifier triplet_builder(String s){
        String[] tab = s.split(":");
        int offset =  Integer.parseInt(tab[0]);
        int length =  Integer.parseInt(tab[1]);
        String term = tab[2];
        return new ReversedIndexIdentifier(offset,length,term);
    }

    public List<ReversedIndexIdentifier> get_mins(List<Integer> index,List<ReversedIndexIdentifier> list){
        List<ReversedIndexIdentifier> mins = new ArrayList<ReversedIndexIdentifier>();

        for (Integer j : index) {
            mins.add(list.get(j));
        }

        return mins;
    }

    public List<Integer> get_mins_indexes(List<ReversedIndexIdentifier> list){
        String min_val="ZZZZZZZ";        //Find better option
        int i=0;
        List<Integer> index = new LinkedList<>();

        for( ReversedIndexIdentifier triplet : list ){
            if(triplet.term.compareTo(min_val)<0){
                index.add(i);
                index.subList(0,index.size()-1).clear();
                min_val=triplet.term;
            }
            else if(triplet.term.compareTo((min_val))==0){
                index.add(i);
            }
            i++;
        }
        return index;
    }


    public ReversedIndexIdentifier get_new_triplet(List<ReversedIndexIdentifier> list,int previous_offset, int previous_total_length) throws Exception {
        String control = list.get(0).term;
        int offset = previous_offset + previous_total_length;
        int length = 0;
        for(ReversedIndexIdentifier current : list){
            if(control!=current.term){
                throw new Exception("Error in new triplet working out processing");
            }
            length+=current.length;
        }

        return new ReversedIndexIdentifier(offset,length,control);
    }

}
