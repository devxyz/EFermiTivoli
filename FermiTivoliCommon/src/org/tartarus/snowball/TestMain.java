package org.tartarus.snowball;

import org.tartarus.snowball.ext.ItalianStemmer;

/**
 * Created by stefano on 20/06/15.
 */
public class TestMain {

    public static void main(String[] args) {
        ItalianStemmer i=new ItalianStemmer();

        i.setCurrent("mangiando");
        i.stem();
        System.out.println(i.getCurrent());
    }
}
