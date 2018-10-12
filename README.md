# Finding and Querying text

## Installation
### Create the dataset
Create a folder called **dataset** at the root of the project. 
Unzip the dataset into the dataset folder. 
Remove the files readchg.txt and readmela.txt.
Run the add_root.sh script. It's use to add a ```<ROOT>``` tag on each files as they are not well formed without that.

## Program steps
### Parsing articles
The first step of the program is to parse each articles and build a partial inverted files.

### Merging the partial inverted files
The second step will merge the partial inverted files in one bigger file.

### Querying text
The last step is to search the most accurate articles for a given list of terms. 