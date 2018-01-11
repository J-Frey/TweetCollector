# TweetCollector

The TweetCollector is a Java-based project that collects live tweets from the Twitter Stream API and writes them to file. The intent for this project is to leave it running on a computer for several days, weeks, or months and then use the resulting data for various analyses.

TweetCollector will collect 25 tweets per second on average. This equates to ~2.1 million tweets per day which will require ~10GB of disk storage. Files can be compressed regularly to reduce file size, however, this is not handled by TweetCollector.

TweetCollector does NOT webcrawl for tweets made in the past.

This project was originally created for a university class in big data management. This project's predecesor was used to collect data for an indexing project in an information retrieval class.

## Getting Started

### Prerequisites

* Windows or Linux (Should run on OS X, but is untested)
* Twitter Account
* Java 8 JDK or OpenJava JDK
* Eclipse (optional)

### Account Setup

Before accessing the Twitter API, create an account and set up the keys and access tokens inside the properties file. This process only needs to be performed once.

1. Copy the 'twitter4j.properties-TEMPLATE' file and rename it to 'twitter4j.properties'
2. Go to https://apps.twitter.com/
3. Create a new app.
4. Go to "Keys and Access Tokens"
5. Copy the Consumer Key and Secret to the twitter4j.properties file.
6. Go to "Your Access Token"
7. Create a new Access Token
8. Copy the Access Token and Access Token Secret to the twitter4j.properties file.

### Execution

Run:

```
java -jar TweetCollector.jar
```

Alternatively, you can import the project into Eclipse and Run it.


### Compile

If you chose to make alterations and compile the source code, compilation is fairly standard:

```
javac *.java
```

## Data Files

By default, TweetCollector collects 10,000 tweets in memory before writing them to file. This is done to prevent TweetCollector from constantly accessing the harddisk. If the application is closed via the console (pressing the Enter key three times), then the tweets currently in memory are dumped to file before closing. If an exception is thrown, tweets will be dumped to file.

Each file contains up to 10,000 lines, each representing a single tweet as a JSON object. Note: The entire file is NOT a JSON object nor is it a JSON array.

More information on tweet objects can be found [here](https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/tweet-object).

## Built With

* [Twitter4J](http://twitter4j.org/en/) - Handles interfacing with Twitter.
* [Maven](https://maven.apache.org/) - Dependency Management

## Authors

* **JFrey** - *Initial work* - [GitHub](https://github.com/J-Frey)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
