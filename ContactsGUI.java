import java.io.*;
import java.util.*;
import javax.swing.*;
public class ContactsGUI{

	public static ArrayList <String> records = new ArrayList <String>();
	/*This method calls other methods*/
	public static void main(String args[]) throws IOException{
		String remove = "Remove", insert = "Insert", search = "Search", quit = "Quit", edit = "Edit", display = "Display", instruction = "";
		boolean valid = validateCommandLineArgs(args);
		if(valid){
			String fileName = args[0].trim();
			boolean fileExists = fileExists(fileName);
			if(fileExists)
				read(fileName);
			while(instruction != quit ){
				instruction = displayMenu(instruction, fileName);
			}
			writeToFile(fileName);
		} 
		else{
			JOptionPane.showMessageDialog(null, "\nYou have not entered valid command line arguments.\nYou should specify one command line argument and it should be a filename ending .txt e.g. MyContacts.txt\nThis file will be created if it does not already exist and your contacts will be written to it.\n");
		} 
	}
	/*This method checks if the file that the user has inputted into the command line exists. 
	If the text file does not exist it will return false. 
	If we are here then the user has entered a valid command line argument.*/
	public static boolean fileExists(String fileName){
		File userFile = new File(fileName);
		if(userFile.exists()){
			return true;
		}
		else{
			return false;
		}
	}
	/* This method displays the menu after the user has chosen a text file. This method shows the user the instructions on how
	to use the contact list. e.g i Surname,Firstname,Data. It will take the first piece of user input, which is the instruction for the program.
	This method sends the user to the specific method that they have chosen to use e.g d goes to display which will display the contact list.
	While this program is running every method always goes back to this method until the user terminates the program.*/
	public static String displayMenu(String instruction, String fileName){
		String remove = "Remove", insert = "Insert", search = "Search", quit = "Quit", edit = "Edit", display = "Display";
		Scanner in = new Scanner (System.in);
		String displayMenu;
		Object menuSelect [] = {"Insert", "Remove", "Edit", "Display", "Search", "Quit"};
		displayMenu = (String)JOptionPane.showInputDialog(null,"Choose one", "Select Option", 1, null, menuSelect, menuSelect[0]);
		if(displayMenu == null)
			JOptionPane.showMessageDialog(null,"You may only exit by selecting the Quit option","Quit",2);
		String command = "";
		instruction = displayMenu;
		boolean cancelSearchInsert = false;
		boolean validInstruction = true;
		if(instruction == insert||instruction == search){
			command = (String)JOptionPane.showInputDialog(null,"Type data", "Data", 3);
			if(command != null){
				command = command.trim();
				String userData []= command.split(",");
				validInstruction = validateInstruction(instruction, userData);
			}
			else
				cancelSearchInsert = true;
		}
		if(validInstruction && instruction != quit){
			if(instruction == insert && !cancelSearchInsert){
				insert(command);
			}
			else if(instruction == display){
				if(records.size() <=0)
						JOptionPane.showMessageDialog(null, "No Data to display!");
				else
					displayContacts(records, 0);
			}
			else if(instruction == remove){
				if(records.size() <=0)
						JOptionPane.showMessageDialog(null, "File is empty!");
				else
					removeContact();
			}
			else if(instruction == search && !cancelSearchInsert){
				if(records.size()<=0)
						JOptionPane.showMessageDialog(null, "File is empty!");
				else
					search(command);
			}
			else if(instruction == edit){
				if(records.size() <=0)
						JOptionPane.showMessageDialog(null, "File is empty!");
				else
					edit();
			}
		}
		else{
			if(!validInstruction){
					JOptionPane.showMessageDialog(null, "Please try again.");
			}
		}
		Collections.sort(records);
		return instruction;
	}
	
	/*This method will check to see if the user has entered a valid command line argument. It will check to see the command line argument is not null, has a length of only 1 and contains .txt. */
	public static boolean validateCommandLineArgs(String args[]){
		boolean valid = true;
		if(args.length != 1){
			valid = false;
		}
		else{
			String end = args[0].substring(args[0].length()-4,args[0].length());
			if (!end.equalsIgnoreCase(".txt")){
				valid = false;
			}
		}
		
		return valid;
	}
	/*This method validates the instruction and user data that the user has inputted in the method displayMenu. It will aslo display an informative error message to the user. It will check to see if the instruction 
	is i,r,e,q or s. It will also check to see if the data that the user inputted is the correct length i.e. there should be at least 3 pieces of information and no more than 6. As well as that it wil check to 
	see if the information given is int he correct format.*/
	public static boolean validateInstruction(String instruction, String userData[]){
		boolean valid = true; 
		String errorMessage = new String();
		String correctInsertFormatMessage = "The correct format for inserting data is i surname,forename,mobile,landline,email,postal\n";
		String correctInsertFormatExample = "Example: i Smith,John,087-1234567 OR i Smith,Jane,Main St Ballymote Co Sligo\n";
		String postalAddressWarning = "Remember when entering your postal address just use spaces to separate Street Town County don't use ','\n";
		String mobNumWarning = "Your mobile number should be in the format of 3 digit prefix and 7 digit suffix separated with a '-' e.g. 087-1234567\n";
		String landLineWarning = "Your landline should be in the format of valid prefix and suffix that is between 5 and 7 digits separated with a '-' e.g. 061-1234567\n";
		String invalidNumbersWarning = "It is possible you are using ',' to separate your Street Town County in your address or that your mobile, landline or email is invalid.\n";
		String correctRemoveFormatMessage = "The correct format for removing data is r surname,forname,[one other piece of data] e.g. Smith,John,087-1234567\n";
		String correctEditFormatMessage = "The correct format to edit a record is e surname,forename,[data to edit],[new data] e.g. Smith,John,0871234567,0877654321\n";
		int numDataPieces = userData.length;
		if(instruction == "Insert"){
			if(numDataPieces < 3){
				errorMessage = "You have provided " + numDataPieces + " piece(s) of data. \n";
				errorMessage += "You need to specify surname, forename followed by at least 1 other piece of information.\n";
				JOptionPane.showMessageDialog(null, errorMessage + correctInsertFormatMessage + correctInsertFormatExample,"Error",0);
				valid = false;
			}
			else if(numDataPieces > 6){
				errorMessage = "You have provided " + numDataPieces + " piece(s) of data. \n";
				errorMessage += "The maximum number of data items should be 6 - surname,forename,mobile,landline,email,postal\n";
				JOptionPane.showMessageDialog(null, errorMessage + correctInsertFormatMessage + correctInsertFormatExample + postalAddressWarning,"Error",0);
				valid = false;
			}
			else if(numDataPieces > 3){
				boolean isPhoneOrEmail = false;
				for(int i=0;i<userData.length && !isPhoneOrEmail;i++){
					if( isMobile(userData[i]) || isLandline(userData[i]) || isEmail(userData[i]) )
						isPhoneOrEmail = true;
				}
				if(!isPhoneOrEmail){
					errorMessage = "You have provided " + numDataPieces + " and the data does not contain a valid mobile or landline number or email address.\n";
					JOptionPane.showMessageDialog(null, errorMessage + invalidNumbersWarning + postalAddressWarning + mobNumWarning + landLineWarning);
					valid = false;
				}
			}
		}
		else if(instruction == "Search"){
			if(numDataPieces != 1){
				errorMessage = "Incorrect number of search terms!\n";
				valid = false;
			}
			else{
				String searchTerm = userData[0];
				if(!isLandLinePrefix(searchTerm) && !isMobilePrefix(searchTerm) && !searchTerm.contains("@")){
					if(searchTerm.length() !=1){
						errorMessage = "Invalid search data!\n";
						valid = false;
					}
				}
				if(searchTerm.contains("@")){
					String splitSearch [] = searchTerm.split("@");
					if(splitSearch.length > 2){
						valid = false;
						errorMessage = "Invalid search data! More that 1 @ character present in the search string\n";
					}
				}
			}
			if(valid == false){
				JOptionPane.showMessageDialog(null, errorMessage + 
													"Valid search commands are as follows: \n" + "(i) By first letter of surname e.g. s W\n" + "(ii) By mobile phone prefix e.g. s 087 \n" + 
													"(iii) By landline prefix e.g. s 062 \n" + "(iv) By email domain e.g. s @gmail.com\n");
			}
		}
		else if(instruction == "")
			valid = false;
		return valid;
	}
	
	/*This method checks if the inputted information is a landline number. If the number inputted does not contain a '-' then it is not a landline. The method will split the phone number into a prefix and a suffix 
	and then proceeds to compare the prefix to valid prefixes contained in a different method. It wil then check to see if the length of the suffix is a valid length.*/
	public static boolean isLandline(String input){
		boolean isLandLine = true;
		int maxSuffix = 7;
		int minSuffix = 5;
		if(!input.contains("-")){
			isLandLine = false;
		}
		else{
			String number [] = input.split("-");
			String prefix = number[0];
			String suffix = number[1];
			int suffixLength = suffix.length();
			if(isLandLinePrefix(prefix)){
				if(suffixLength <=5 && suffixLength >= 7){
					isLandLine = false;
				}
			}
			else{
				isLandLine = false;
			}
		}
		return isLandLine;
	}
	/*This method checks if the inputted information is a mobile number by checking if it has a valid prefix and its suffix has 7 numbers.*/
	public static boolean isMobile(String input){
		boolean isMobile = false;
		int prefix = 0;
		int suffix = 1;
		int mobSuffixLength = 7;
		if(input.contains("-")){
			String splitMobile [] = input.split("-");
			if(isMobilePrefix(splitMobile[prefix])){
				if(splitMobile[suffix].length() == mobSuffixLength){
					isMobile = true;
				}
			}
		}
		return isMobile;
	}
	/*This method checks if the inputted information is an email by looking for both an '@' symbol and a '.' . If it contains neither of these than the data is not 
	a valid email.*/
	public static boolean isEmail(String input){
		boolean isEmail;
		if(input.contains("@") && input.contains(".")){
			if(input.split("@").length == 2){
				isEmail = true;
			}
			else{
				isEmail = false;
			}
		}
		else{
			isEmail = false;
		}
		return isEmail;
	}
	/*This method checks if the landline has the correct prefixes to be a landline.If it contains a - (dash) it is a landline. It has valid prefixes stored in an array and will be checked each time it's called*/
	public static boolean isLandLinePrefix(String data){
		boolean isPrefix = false;
		boolean found = false;
		String [] prefixes = {"01", "02", "021", "022", "023", "024", "025", "026", "027", "028", 
		                      "029", "0402", "0404", "041", "042", "043", "044", "045", "046", "047",
		                      "049", "0504", "0505", "051", "052", "053", "056", "057", "058", "059",
		                      "061", "062", "063", "064", "065", "066", "067", "068", "069", "071", "074",
		                      "090", "091", "093", "094", "095", "096", "097", "098", "099"};
		for(int i=0;i<prefixes.length && !found;i++){
			if(data.equalsIgnoreCase(prefixes[i])){
				isPrefix = true;
			}
		}
		return isPrefix;
	}
	/*Similar to the isLandlinePrefix method but checks for different prefixes that are valid mobile numbers.*/
	public static boolean isMobilePrefix(String data){
		boolean isPrefix = false;
		boolean found = false;
		String [] prefixes = {"087", "086", "085", "083", "089"};
		for(int i=0;i<prefixes.length && !found;i++){
			if(data.equalsIgnoreCase(prefixes[i])){
				isPrefix = true;
			}
		}
		return isPrefix;
	}
	
	/*This method will edit the contacts information. It has already been validated so we know that its of size 4 and the old information is the same type as the new information
	This method will loop throught the records and will replace the old the information with the new.*/
	public static void edit(){
		int numToEdit = 0;
		String userToEdit = "";
		displayContacts(records, 1);
		boolean errorIn = false;
		while(!errorIn){
			try{
				errorIn = true;
				numToEdit = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter number of contact you wish to edit"));
			}
			catch(NumberFormatException ex){
				JOptionPane.showMessageDialog(null, "Error. Please enter number");
				errorIn = false;
					
			}
		}
		for(int i = 0; i < records.get(numToEdit - 1).length(); i++){
			userToEdit = records.get(numToEdit - 1);
		}
		if(numToEdit < records.size() || numToEdit >= 1){
			if(userToEdit != null){
				Object menuSelect [] = {"Surname", "Forename", "Landline", "Mobile number", "Email address", "Postal address"};
				String displayMenuData = (String)JOptionPane.showInputDialog(null,"Choose one", "Select Option", 1, null, menuSelect, menuSelect[0]);
				String changes="";
				String newRecord = userToEdit;
				String instruction = displayMenuData;
				String currentRecord[] = userToEdit.split(",");
				String changedRecord;
				if(instruction == menuSelect[0])
				{
					changes =JOptionPane.showInputDialog(null,"Please enter new Surname","Surname",3);
					if (changes.equals(""))
						JOptionPane.showMessageDialog(null,"No input given","Error",0);
					else
					currentRecord[0] = changes;
					changedRecord = currentRecord[0];
					for(int i =1;i < currentRecord.length;i++)
						changedRecord +="," + currentRecord[i];
					records.remove(userToEdit);
					records.add(changedRecord);
				}
				else if(instruction == menuSelect[1])
				{
					changes = JOptionPane.showInputDialog(null,"Please enter new Forename","Forename",3);
					if (changes.equals(""))
						JOptionPane.showMessageDialog(null,"No input given","Error",0);
					else
					currentRecord[1] = changes;
					changedRecord = currentRecord[0];
					for(int i =1;i < currentRecord.length;i++)
						changedRecord +="," + currentRecord[i];
					records.remove(userToEdit);
					records.add(changedRecord);
				}
				else if(instruction == menuSelect[2])
				{
					changes =JOptionPane.showInputDialog(null,"Please enter new Landline","Landline",3);
					if (changes.equals(""))
						JOptionPane.showMessageDialog(null,"No input given","Error",0);
					else
					currentRecord[2] = changes;
					changedRecord = currentRecord[0];
					for(int i =1;i < currentRecord.length;i++)
						changedRecord +="," + currentRecord[i];
					records.remove(userToEdit);
					records.add(changedRecord);
				}
					else if(instruction == menuSelect[3])
				{
					changes =JOptionPane.showInputDialog(null,"Please enter new Mobile","Mobile",3);
					if (changes.equals(""))
						JOptionPane.showMessageDialog(null,"No input given","Error",0);
					else
					currentRecord[3] = changes;
					changedRecord = currentRecord[0];
					for(int i =1;i < currentRecord.length;i++)
						changedRecord +="," + currentRecord[i];
					records.remove(userToEdit);
					records.add(changedRecord);
				}
				else if(instruction == menuSelect[4])
				{
					changes =JOptionPane.showInputDialog(null,"Please enter new Email","Email",3);
					if (changes.equals(""))
						JOptionPane.showMessageDialog(null,"No input given","Error",0);
					else
					currentRecord[4] = changes;
					changedRecord = currentRecord[0];
					for(int i =1;i < currentRecord.length;i++)
						changedRecord +="," + currentRecord[i];
					records.remove(userToEdit);
					records.add(changedRecord);
				}
				else if(instruction == menuSelect[5])
				{
					changes =JOptionPane.showInputDialog(null,"Please enter new Postal address","Postal Address",3);
					if (changes.equals(""))
						JOptionPane.showMessageDialog(null,"No input given","Error",0);
					else
					currentRecord[5] = changes;
					changedRecord = currentRecord[0];
					for(int i =1;i < currentRecord.length;i++)
						changedRecord +="," + currentRecord[i];
					records.remove(userToEdit);
					records.add(changedRecord);
				}
			}
		}
		else{
			JOptionPane.showMessageDialog(null,"Number too large or below 1. ","Error",0);
		}
	}
	
	/*This method wil insert the user input into the text file as a contact. It will write the formatted date to the global array list */
	public static void insert(String input){
		String [] formattedData = format(input);
		String dataToWrite = formattedData[0];
		for(int i=1;i<formattedData.length;i++){
			if(formattedData[i] != null){
				dataToWrite = dataToWrite + ","+ formattedData[i];
			}
			else{
				dataToWrite = dataToWrite + ",";
			}
		}
		records.add(dataToWrite);
	}
	
	/*This method will write an array list to a file overwriting any current file that exists. It will open a file to append and then write data to the file.*/
	public static void writeToFile(String fileName) throws IOException{	
		FileWriter fw = new FileWriter(fileName);
		PrintWriter pw = new PrintWriter(fw);
			
		for(int i=0;i<records.size();i++){
			String input = records.get(i);
			pw.println(input);
		}	
		pw.close();
	}
	
	/*This method will format the data to write to file. It will set the surname first as that's mandatory and the first name second as well.
	It will then check the rest of the information and organise it depending on whether it's a landline,mobile,email or postal address.*/
	public static String [] format(String input){
		String [] userData = input.split(","); 
		String [] formattedData = new String [6];
		for(int i=0;i<formattedData.length;i++){
			formattedData[i] = new String(" ");
		}
		String dataToWrite;
		formattedData[0] = userData [0];
		formattedData[1] = userData [1];
		for(int i=2;i<userData.length;i++){
			if(isLandline(userData[i])){
				formattedData[2] = userData[i];
			}
			else if(isMobile(userData[i])){
				formattedData[3] = userData[i];
			}
			else if(isEmail(userData[i])){
				formattedData[4] = userData[i];
			}
			else{
				formattedData[5] = userData[i];
			}
		}
		return formattedData;
	}
	
	/*This method will read the data from the file.*/
	public static void read(String fileName) throws IOException{
		FileReader fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		while(line != null){
			records.add(line);
			line = br.readLine();
		}
		br.close();
	}
	
	/*This method will display the contacts in groups of 4 and will ask if the user wants to continue or quit the program. */
	public static String displayContacts(ArrayList <String> displayArrayList, int methodNumber){
		String labels [] = {"Surname: " , "Forename: ", "Landline: ", "Mobile: ", "Email: ", "Postal Address:"};
		Object allData [] = new Object [displayArrayList.size()];
		for(int i = 0; i < displayArrayList.size(); i++){
			if(methodNumber == 1){
				allData [i] = (i + 1) +  ":   " + displayArrayList.get(i);
			}
			else{
				allData [i] = displayArrayList.get(i);
			}
		}
		String displayList = (String)JOptionPane.showInputDialog(null, "List of contacts", "Contacts", 1, null, allData, allData[0]);
		return displayList;
	} 
	/*This method will remove any information from the contacts that the user requests.It will check the surname and the firstname and then compare the third piece of the user data against
	the rest of the current record. If the user tries to remove information that is not contained in the file, an appropriate error message is displayed.*/
	public static void removeContact(){
		int numToRemove = 0;
		displayContacts(records, 1);
		boolean errorIn = false;
		while(!errorIn){
			try{
				errorIn = true;
				numToRemove = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter number of contact you wish to remove"));
			}
			catch(NumberFormatException ex){
				JOptionPane.showMessageDialog(null, "Error. Please enter number");
				errorIn = false;
			}
		}
		if(numToRemove < records.size() || numToRemove >= 1){
			records.remove(numToRemove - 1);
		}
		else{
			JOptionPane.showMessageDialog(null,"Number too large or below 1. ");
		}
	}
	
	/*This method will search the array list for whatever the user searched for. It will then display the serach results.*/
	public static void search(String instruction){
		int surname = 0;
		int mobile = 3;
		int landline = 2;
		int email = 4;
		int prefix = 0;
		char at = '@';
		boolean isLandLine = isLandLinePrefix(instruction);
		boolean isMobile = isMobilePrefix(instruction);
		boolean isEmail = false;
		boolean isSurname = false;
		if(! isLandLine && !isMobile){
			if(instruction.charAt(0) == at){
				isEmail = true;
			}
			else{
				isSurname = true;
			}
		}
		
		ArrayList <String> searchResults = new ArrayList<String>();
		for(int i=0;i<records.size();i++){
			String currentRecord [] = records.get(i).split(",");
			if(isLandLine){
				if(!currentRecord[landline].equals(" ")){
					String splitLandLine [] = currentRecord[landline].split("-");
					if(splitLandLine[prefix].equalsIgnoreCase(instruction) ){
						searchResults.add(records.get(i));
					} 
				}
			}
			else if(isMobile){
				if(!currentRecord[mobile].equals(" ")){
					String splitMobile [] = currentRecord[mobile].split("-");
					if(splitMobile[prefix].equalsIgnoreCase(instruction) ){
						searchResults.add(records.get(i));
					} 
				}
			}
			else if(isEmail){
				if(!currentRecord[email].equals( " ")){
					String splitEmail [] = currentRecord[email].split("@");
					String searchString = "@" + splitEmail[1];
					if(searchString.equalsIgnoreCase(instruction)){
						searchResults.add(records.get(i));
					}
				}
			}
			else{
				String firstLetter = currentRecord[surname].substring(0,1);
				if(firstLetter.equalsIgnoreCase(instruction)){
					searchResults.add(records.get(i));
				}
			}
		}
		if(searchResults.size() > 0){
			String notNeeded = displayContacts(searchResults, 0);
		}
		else{
			JOptionPane.showMessageDialog(null,"There are no contacts on file matching your search term " + instruction);
		}
	}
}