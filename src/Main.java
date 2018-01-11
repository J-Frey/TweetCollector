import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class Main {
	/**
	 *  Date format for filenames with a timestamp
	 */
	private static final SimpleDateFormat DATE_FILE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	
	/**
	 *  Number of tweets to collect in memory before dumping to file
	 */
	static final Integer DUMP_THRESHOLD = 10000;
	
	/**
	 * Number of tweets to collect before printing a update notification in console.
	 */
	static final Integer MILESTONE_PRINTOUT_THRESHOLD = 500;
	
	/**
	 *  Number of tweets collected in session.
	 */
	static Integer counter = 0;
	
	/**
	 * Number of tweets collected overall.
	 */
	static Long globalCounter = 0L;
	
	/**
	 * Ad-Hoc in memory cache for tweets. 
	 * Prevents hammering the IO for EVERY tweet. 
	 * Dumps list to file when size is equal to DUMP_THRESHOLD
	*/
	static List<String> tweetJSONs = new ArrayList<String>();
	
	/**
 	 * Collect tweets marked with languages in the filter
	 * If empty, collects tweets of all languages
	 * List of language codes: https://dev.twitter.com/web/overview/languages
	 */
	static List<String> LANGUAGE_FILTER = new ArrayList<>();
	//static List<String> LANGUAGE_FILTER = Arrays.asList( "en" );
	
	
	public static void main(String[] args) {
		// Setup configurations through the property file
		System.setProperty ("twitter4j.loggerFactory", "twitter4j.internal.logging.NullLoggerFactory");
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(false);
		cb.setJSONStoreEnabled(true);
		
		// Create Twitter Stream
		TwitterStreamFactory fact = new TwitterStreamFactory(cb.build());
		TwitterStream twitterStream = fact.getInstance();
		
		// Add a listener to the stream
		StatusListener listener = new StatusListener() {
			@Override
			public void onStatus(Status status) {
				// Collect tweet if the language is in the language filter OR the filter is empty
				if(LANGUAGE_FILTER.isEmpty() || LANGUAGE_FILTER.contains(status.getLang())) {
					CollectTweet(status);
				}
			}

			@Override
			public void onException(Exception e) { 
				dumpFiles(); 
				e.printStackTrace(); 
			}
			
			@Override
			public void onTrackLimitationNotice(int arg0) {}
			
			@Override
			public void onStallWarning(StallWarning arg0) {}
			
			@Override
			public void onScrubGeo(long arg0, long arg1) {}
			
			@Override
			public void onDeletionNotice(StatusDeletionNotice arg0) {}
		};
		
		twitterStream.addListener(listener);

		//Disable this if filtering
		twitterStream.sample();
		
		try{
			System.out.println("Press <Enter> three times to exit.");
			System.in.read();
			System.out.println("Press <Enter> two times to exit.");
			System.in.read();
			System.out.println("Press <Enter> to exit.");
			System.in.read();
		}catch(IOException e){
			e.printStackTrace();
		}

		System.out.println("Entering Exit Setup");
		
		// Dump current data
		System.out.println("Dumping current files in memory...");
		dumpFiles();
		
		// Close stuff
		System.out.println("Closing connections...");
		twitterStream.clearListeners();
		twitterStream.cleanUp();
		
		// Exit
		System.out.println("Program End");
	}

	public static synchronized void CollectTweet(Status tweet) {
		// If enough tweets have been collected, print an update notification
		if(counter > 0 && counter % MILESTONE_PRINTOUT_THRESHOLD == 0){
			System.out.println(String.format("\t%s tweets collected. %s tweets collected in total.",
					counter.toString(),
					globalCounter.toString()
					));
		}
		
		// Check counter, if enough tweets have been collected then dump to a file
		if(counter >= DUMP_THRESHOLD) {
			dumpFiles();
		}

		// Collect tweet
		tweetJSONs.add(TwitterObjectFactory.getRawJSON(tweet));
		counter++;
		globalCounter++;
	}

	public static synchronized void dumpFiles() {
		if(counter == 0) {
			System.out.println("\n" + counter.toString() + " tweets collected. Nothing to dump.\n");
			return;
		}
		
		System.out.println("\n" + counter.toString() + " tweets collected. Dumping tweets to file.\n");
		
		// Reset Counter
		counter = 0;
		
		// Data direcotry where all files will be stored
		File dir = new File("TwitterData/");
		
		// If directory doesn't exist, then create it
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		// Data file with timestamp in file name
		// NOTE: There is no check to ensure if this filename already exists!
		File dataFile = new File("TwitterData/tweets-" + DATE_FILE_FORMAT.format(new Date()) + ".twt");
		
		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataFile), "utf-8"));
			for (String json : tweetJSONs) {
				writer.write(json + "\n");
			}

			//Clear the list
			tweetJSONs.clear();
		}
		
		catch(IOException e) {
			e.printStackTrace();
		}
		
		finally {
			if(writer != null) {
				try{
					writer.close();
					writer = null;
				}
				catch(IOException e2) {
					e2.printStackTrace();
				}
			}
		}
	}
}
