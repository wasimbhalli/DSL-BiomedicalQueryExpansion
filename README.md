# Biomedical Query Expansion (Data Science Lab, KICS-UET)

## Pre-requisties ###

* Install Jdk 7 or higher
* Install Jre latest version
* Install Eclipse
* Install manven latest version
	* Open terminal and type: _sudo apt-get install maven_
* Download and install the [solr-6.6.0](http://lucene.apache.org/solr/downloads.html) or higher from it official site
* Download genomic data repository from [TREC 2007 Genomics Track Data](http://skynet.ohsu.edu/trec-gen/2007data.html)


## How to configure the solr
* Run solr service by default it use port 8983. To check in your browser type: _localhost:8983_
+ Create a new core or collection, the default core/colletion directory is 
	/var/solr/data
* Once you download your data repositoty, extract them and combine all the files under one directory, its require about 9.8 GB of space
* Now Index the data for you created core/collection:
* Solr indexed your data according to your default **solrconfig.xml** schema but you can define and specify your own fields
	### Specifiy your own fields in solr:
    you can Update **_solrschema.xml_** and **_managed-schema_** located in your new created core/collection directory files by adding new fields 
	#### How To add new fields
    * Open **_/var/solr/data/<core/collection name>/conf/_** --> managed-schema, solrschema.xml
    	* In _solrschema.xml_ file: Search
    		```xml
            <requestHandler name="/update/extract" startup="lazy" class="solr.extraction.ExtractingRequestHandler"/>
		* Add a new field inside the above tag
			```xml
    		<str name="capture">body</str>
        * To add your own replace the body with your own field name
        * Save and exit the file
        * In _managed-schema_ file: Search <br>
        	```xml
        	<field name="_text_" type="text_general" multiValued="true" indexed="true" stored="false" />
        * Add a new tag uder the "**_text_**" field
        	```xml
            <field name="body" type="text_general" indexed="true" stored="true"/>
		* To add your own replace the "**_body_**" with your own field name 
	* Restart your solr by type command <br>
		_sudo service solr restart_
        * If your solr get error, Check you configuration files properly<br>
**_Note_:** Name must be same in managed-schema and solrconfig.xml files


## How to setup files and compile the code 
* Open terminal in you project root directory and type<br>
	_mvn compile_
    
    It will compiles all the dependencies in your **_pom.xml_** file

* Some files are required to run code<br>
_Following files must be included in you **resources** dir_
1. Downlaod [_trecgen2007.gold.standard.tsv.txt_](http://skynet.ohsu.edu/trec-gen/data/2007/trecgen2007.gold.standard.tsv.txt)
2. Downlaod [_2007topics.txt_](http://skynet.ohsu.edu/trec-gen/data/2007/2007topics.txt)
4. Add a folder name **_script_** under the resource dir
5. Download [_trecgen2007_score.py_](http://skynet.ohsu.edu/trec-gen/data/2007/trecgen2007_score.py) and save under **_script_** dir
6. Now create a dir named **_DocResult_** under resource dir  --> _(This directory will be used for the output of results comparision)_
