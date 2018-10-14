package com.github.quadinsa5if.findingandqueryingtext.tokenizer;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 *  Iterate through a text file by using a delimiter
 *  Don't hesitate to improve that class!
 * */
public class FileIterator implements Iterator<String> {
    private final FileInputStream fis;
    private BufferedReader bf;
    private StringBuilder sb;
    private String s;
    private char delimiter;

    public FileIterator(File file, char c) throws IOException {
            this.fis = new FileInputStream(file);
            this.bf = new BufferedReader((new InputStreamReader(fis)));
            this.delimiter = c;
            this.s=get_line(this.delimiter);

    }

    @Override
    public boolean hasNext() {
        return this.s != null;
    }

    @Override
    public String next() throws NoSuchElementException {
        String returnstring = this.s;
        try {
            if(this.s == null){
                throw new NoSuchElementException("Next line not available");
            }else{
                this.s =this.get_line(this.delimiter);
                if( this.s==null && this.bf != null){
                    this.bf.close();
                    this.bf = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NoSuchElementException( "Exception caught in FileLineIterator.next() " + e );
        }
        return returnstring;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException( "FileLineIterator.remove() is not supported" );
    }

    @Override
    protected void finalize() throws Throwable
    {
        try
        {
            this.s = null;
            if( this.bf != null ) try{ this.bf.close(); } catch( Exception ex ) { }
            this.bf=null;
        }
        finally
        {
            super.finalize();
        }
    }

    @Nullable
    private String get_line(@NotNull char delimiter) throws IOException {
        int c;
        while((c = this.bf.read()) != -1){
            char ch = (char) c;
            if(ch==delimiter){

                return this.sb.toString();
            }else{
                this.sb.append(ch);
            }
        }
        this.sb.setLength(0);
        return null;
    }
}



