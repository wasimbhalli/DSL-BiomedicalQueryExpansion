/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pk.edu.kics.dsl.qa.services;

import java.io.IOException;

import pk.edu.kics.dsl.qa.util.GenericObject;
import pk.edu.kics.dsl.qa.util.replace_UTF8;

/**
 *
 * @author Muhammad Wasim
 */
/**
 * @author sajju
 *
 */
public class MetaMapServiceCall {
    
    GenericObject metaMapServiceObject = new GenericObject(100, "wasimbhalli", "UMLSM@ta1");
        
    public String getSimilarWords(String keywords) {
        String results = null;
        metaMapServiceObject.setField("Email_Address", "cmwasim@gmail.com");
        
        //StringBuffer buffer = new StringBuffer(keywords);
        //String bufferStr = buffer.toString();
        try {
        	//replace UTF-8 character's
			metaMapServiceObject.setField("APIText", replace_UTF8.ReplaceLooklike(keywords));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
        metaMapServiceObject.setField("KSOURCE", "1516");
        // Optional field, program will run default MetaMap if not specified
        metaMapServiceObject.setField("COMMAND_ARGS", "-Ava --JSONf 2 -V USAbase");
        
        // Submit the job request
        try
        {
           results = metaMapServiceObject.handleSubmission();
        } catch (RuntimeException ex) {
           System.err.println("");
           System.err.print("An ERROR has occurred while processing your");
           System.err.println(" request, please review any");
           System.err.print("lines beginning with \"Error:\" above and the");
           System.err.println(" trace below for indications of");
           System.err.println("what may have gone wrong.");
           System.err.println("");
           System.err.println("Trace:");
           ex.printStackTrace();
        }
        
        return results;
    }
    public static void main(String args[])
    {
    MetaMapServiceCall service = new MetaMapServiceCall();
	System.out.println(service.getSimilarWords("what is cancer"));
}
}
