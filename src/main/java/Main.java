import java.io.File;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.*;
import java.util.regex.Pattern;


import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;


import edu.stanford.nlp.simple.*;
import java.util.Arrays;
import java.util.List;

class Tess {
    public static void main(String[] args){
        Tesseract tesseract=new Tesseract();
        File img= new File("C:\\Users\\allur\\DemoTesseract\\pics\\busicard4.jpg");
        String text="";
        try{
            tesseract.setDatapath("C:\\Users\\allur\\DemoTesseract\\src\\main\\resources");
            tesseract.setLanguage("eng");
            text= tesseract.doOCR(img);
            //System.out.println(text);

        }
        catch (TesseractException e){
            e.printStackTrace();
            //System.out.println(3);
        }

        //EXTRACTION EMAIL
        String[] emailmatches = Pattern.compile("\\S+@\\S+\\.\\S+")
                .matcher(text)
                .results()
                .map(MatchResult::group)
                .toArray(String[]::new);
        System.out.print("Emails:");
        System.out.println(Arrays.toString(emailmatches));
          text=text.replace(emailmatches[0], ""); //removes email from text

        //EXTRACTION PHONE NUMBER
        Iterator<PhoneNumberMatch> existsPhone= PhoneNumberUtil.getInstance().findNumbers(text, "IN").iterator();
        while (existsPhone.hasNext()) {
            String tempPhone= existsPhone.next().rawString();
            System.out.println("Phone Number = " + tempPhone);
            text=text.replace(tempPhone, ""); //removes phone numbers
        }

        //EXTRACTION WEBSITE
        String[] websitematches = Pattern.compile("\\S+\\.com+")
                .matcher(text)
                .results()
                .map(MatchResult::group)
                .toArray(String[]::new);
        System.out.print("Websites: ");
        System.out.println(Arrays.toString(websitematches));
       // text=text.replace(websitematches[0], ""); //removes websites




        text=text.replaceAll("\\R", " "); //make everything one line (remove all /n)
        //System.out.println("CHANGED TEXT IS: "+text);


        //EXTRACTION NAME
        Document doc = new Document(text);
        String name="";
        for (Sentence sent : doc.sentences()) {  // Will iterate over two sentences
            // We're only asking for words -- no need to load any models yet


            //System.out.println(sent.nerTags().toArray()[0].equals("PERSON"));
            List<String> results1 = sent.nerTags();
            //System.out.println(Arrays.toString(results1.toArray()));
            for(int i=0;i< results1.size();i++){
                if(results1.toArray()[i].equals("PERSON")){
                    name=name + (text.split(" ")[i]) + " ";
                }
            }
           // System.out.println(name);
        }
        System.out.println("Name: "+name);

        text=text.replace(name, ""); // removes names


        //Extract Address
        String workingUSAddressRegex= "[1-9][0-9]{0,5}[,]{0,1}[ ]+.*,{0,1} +[A-Za-z]{2},{0,1} +[0-9]{5}";
        String[] addressMatches = Pattern.compile(workingUSAddressRegex)
                .matcher(text)
                .results()
                .map(MatchResult::group)
                .toArray(String[]::new);
        System.out.print("Address: ");
        System.out.println(Arrays.toString(addressMatches));



    }
}
