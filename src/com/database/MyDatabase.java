package com.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MyDatabase {
	//Maps for creating every index file
	static Map<Integer, Integer> idSortmap ;
	static Map<String, ArrayList<Integer>> lnameSortmap ;
	static Map<String, ArrayList<Integer>> stateSortmap;
	static int finaloffset=0;
	public static void readCsv(){
		//csv location
		String csvFile="C:\\Text Books\\DB\\project2\\us-500.csv";
		BufferedReader br = null;
		//db file location
		File file = new File("C:\\Text Books\\DB\\project2\\data.db");
		//Loads the index of each record into maps, if files are not already present
		if(!file.exists()){
			String line = "";
			int offset=0;
			HashMap<Integer, Integer> idMap= new HashMap<Integer, Integer>();
			HashMap<String, ArrayList<Integer>> lnameMap= new HashMap<String,ArrayList<Integer>>();
			HashMap<String, ArrayList<Integer>> stateMap= new HashMap<String, ArrayList<Integer>>();
			try{

				int count=0;
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				br = new BufferedReader(new FileReader(csvFile));
				BufferedWriter bw = new BufferedWriter(fw);
				while ((line = br.readLine()) != null) {
					if(count==0){
						count++;
						continue;
					}
					String [] arr=new String[13];
					arr=line.split("\",\"|\",|,\"");
					arr[0]=arr[0].replace("{", "");
					arr[0]=arr[0].replace("\"", "");
					arr[2]=arr[2].replace("{", "");
					arr[2]=arr[2].replace("\"", "");
					arr[7]=arr[7].replace("{", "");
					arr[7]=arr[7].replace("\"", "");

					idMap.put(Integer.parseInt(arr[0]), offset);

					if(lnameMap.containsKey(arr[2])){
						ArrayList<Integer> newArr=	lnameMap.get(arr[2]);
						newArr.add(offset);
						lnameMap.put(arr[2], newArr);
					}else{
						ArrayList<Integer> newArr= new ArrayList<Integer>(); 
						newArr.add(offset);
						lnameMap.put(arr[2],newArr);
					}
					if(stateMap.containsKey(arr[7])){
						ArrayList<Integer> newArr=	stateMap.get(arr[7]);
						newArr.add(offset);
					}else{
						ArrayList<Integer> newArr= new ArrayList<Integer>(); 
						newArr.add(offset);
						stateMap.put(arr[7],newArr);
					}

					line+="|\n";
					bw.write(line);
					offset+=line.length();

				}
				finaloffset=offset;
				bw.close();
				idSortmap = new TreeMap <Integer, Integer>(idMap);
				lnameSortmap = new TreeMap <String, ArrayList<Integer>>(lnameMap);
				stateSortmap = new TreeMap <String, ArrayList<Integer>>(stateMap);

				makeindex("id");
				makeindex("lname");
				makeindex("state");

			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		//Reloads the index of each record into maps, if the files already exists
		else{
			File id=new File("C:\\Text Books\\DB\\project2\\id.ndx");
			File lname=new File("C:\\Text Books\\DB\\project2\\lname.ndx");
			File state=new File("C:\\Text Books\\DB\\project2\\state.ndx");
			if(!id.exists()||!lname.exists()||!state.exists()){

				System.out.println("One or more of the index file(s) are missing");
			}else{

				try{
					String idLine="";
					idSortmap= new TreeMap<Integer, Integer>();
					lnameSortmap= new TreeMap<String, ArrayList<Integer>>();
					stateSortmap= new TreeMap<String, ArrayList<Integer>>();
					BufferedReader idbr = new BufferedReader(new FileReader(id));
					while ((idLine = idbr.readLine()) != null) {
						String [] ids=idLine.split(",");
						idSortmap.put(Integer.parseInt(ids[0]), Integer.parseInt(ids[1]));
					}				
					BufferedReader lnamebr = new BufferedReader(new FileReader(lname));
					while ((idLine = lnamebr.readLine()) != null) {
						//
						String [] ids=idLine.split("\\[");
						ArrayList<Integer> lnameOff= new ArrayList<Integer>();
						ids[1].replaceAll("\\[", "");
						ids[1].replaceAll("\\]", "");
						//System.out.println(idLine);
						String [] lms=ids[1].split(",");
						for(int i=0;i<lms.length;i++){
							lms[i]=lms[i].replace("[", "");
							lms[i]=lms[i].replace("]", "");
							lnameOff.add(Integer.parseInt(lms[i]));

						}
						ids[0]=ids[0].replaceAll(",","").trim();
						lnameSortmap.put(ids[0], lnameOff);

					}
					BufferedReader statebr = new BufferedReader(new FileReader(state));
					while ((idLine = statebr.readLine()) != null) {
						String [] ids=idLine.split("\\[");
						ArrayList<Integer> stateOff= new ArrayList<Integer>();

						String [] sts=ids[1].split(",");

						for(int i=0;i<sts.length;i++){
							sts[i]=sts[i].replace("[", "");
							sts[i]=sts[i].replace("]", "");
							sts[i]=sts[i].replace(" ", "");

							stateOff.add(Integer.parseInt(sts[i]));

						}
						ids[0]=ids[0].replaceAll(",","").trim();
						stateSortmap.put(ids[0], stateOff);
					}
					idbr.close();
					lnamebr.close();
					statebr.close();
				}catch(IOException io){

				}

			}

		}


	}
	public static void makeindex(String ndx){
		try{
			//Writing into the ID index file
			if(ndx.equalsIgnoreCase("id")){
				String idstring=idSortmap.toString();
				String [] idsplit=idstring.split(",");
				File idNdx=new File("C:\\Text Books\\DB\\project2\\id.ndx");
				FileWriter f = new FileWriter(idNdx.getAbsoluteFile());
				BufferedWriter b = new BufferedWriter(f);


				for(int i=0;i<idsplit.length;i++){
					String [] id=idsplit[i].split("=");
					//b.write("");
					for(int j=0;j<id.length;j++){
						id[j]=id[j].replace("{", "");
						id[j]=id[j].replace("}", "");
						id[j]=id[j].replace(" ", "");
						String temp=id[j].replaceAll("\"", "");
						b.write(temp);
						if(j==(id.length-2)){
							b.write(",");
						}
					}
					b.write("\n");

				}
				b.close();

			}//Writing into the state index file
			else if(ndx.equalsIgnoreCase("state")){
				String statestring=stateSortmap.toString();
				statestring=statestring.replaceAll("\"", "");
				statestring=statestring.replace("{", "");
				statestring=statestring.replace("}", "");
				String [] statesplit=statestring.split("],");
				File stateNdx=new File("C:\\Text Books\\DB\\project2\\state.ndx");
				FileWriter fs = new FileWriter(stateNdx.getAbsoluteFile());
				BufferedWriter bs = new BufferedWriter(fs);

				//System.out.println(statestring);
				for(int i=0;i<statesplit.length;i++){

					String [] id=statesplit[i].split("=");

					for(int j=0;j<id.length;j++){

						id[j]=id[j].replace("{", "");
						id[j]=id[j].replace("}", "");
						//id[j]=id[j].replace(" ", "");
						String temp=id[j].replaceAll("\"", "");
						bs.write(temp);
						if(j==(id.length-2)){
							bs.write(",");
						}
					}
					bs.write("]\n");

				}
				bs.close();

			}//Writing into the lastname index file
			else if(ndx.equalsIgnoreCase("lname")){
				String lnamstring=lnameSortmap.toString();
				String [] lnamsplit=lnamstring.split("],");
				File lnamNdx=new File("C:\\Text Books\\DB\\project2\\lname.ndx");
				FileWriter fl = new FileWriter(lnamNdx.getAbsoluteFile());
				BufferedWriter bl = new BufferedWriter(fl);


				for(int i=0;i<lnamsplit.length;i++){
					String [] id=lnamsplit[i].split("=");
					//bl.write("<");
					for(int j=0;j<id.length;j++){
						id[j]=id[j].replace("{", "");
						id[j]=id[j].replace("}", "");

						//id[j]=id[j].replace("[", "");
						//id[j]=id[j].replace("]", "");
						id[j]=id[j].replace(" ", "");
						String temp=id[j].replaceAll("\"", "");
						bl.write(temp);
						if(j==(id.length-2)){
							bl.write(",");
						}
					}
					bl.write("]\n");

				}
				bl.close();
			}
		}catch(IOException io){

		}
	}
	//Select funtion based on last name/state
	public static void select(String index){
		String type="";
		try{
			if(lnameSortmap.containsKey(index)){
				type="lname";
			}
			else if(stateSortmap.containsKey(index)){
				type="state";
			}
			else{
				System.out.println("There are no indices defined for the given data or there are no records having this field :"+index);
			}
			if(!type.equals("")){
				System.out.println("Select: "+index);
				if(type.equals("state")){
					//getting the index from the map to access the db file
					ArrayList<Integer> offsets= stateSortmap.get(index);
					RandomAccessFile file = new RandomAccessFile("C:\\Text Books\\DB\\project2\\data.db", "rw");

					for(int i=0;i<offsets.size();i++){
						int offset=offsets.get(i);
						file.seek(offset);
						while(true){
							char c=(char) file.readByte();
							if(c == '|'){
								break;
							}
							System.out.print(c);
							offset++;

						}
						System.out.println();
					}
					file.close();
				}
				else{
					//getting the index from the map to access the db file
					ArrayList<Integer> offsets= lnameSortmap.get(index);
					RandomAccessFile file = new RandomAccessFile("C:\\Text Books\\DB\\project2\\data.db", "rw");

					for(int i=0;i<offsets.size();i++){
						int offset=offsets.get(i);
						file.seek(offset);
						while(true){
							char c=(char) file.readByte();
							if(c == '|'){
								break;
							}
							System.out.print(c);
							offset++;

						}
						System.out.println();
					}
					file.close();
				}
			}
		}
		catch(FileNotFoundException f){
			f.printStackTrace();
		}
		catch(IOException io){
			io.printStackTrace();
		}

	}
	//Select funtion based on ID
	public static void select(Integer index){
		try{

			if(idSortmap.containsKey(index)){
				//getting the index from the map to access the db file
				Integer offsets= idSortmap.get(index);
				RandomAccessFile file = new RandomAccessFile("C:\\Text Books\\DB\\project2\\data.db", "rw");

				System.out.println("Select: "+index);
				//printing each line
				file.seek(offsets);
				while(true){
					char c=(char) file.readByte();
					if(c == '|'){
						break;
					}
					System.out.print(c);
					offsets++;

				}
				System.out.println();

				file.close();

			}else{
				System.out.println("There are no indices defined for the given data or there are no records having this field :"+index);
			}
		}catch(FileNotFoundException f){
			f.printStackTrace();
		}
		catch(IOException io){
			io.printStackTrace();
		}
	}
	public static void delete(String index){
		String type="";
		if(lnameSortmap.containsKey(index)){
			type="lname";
		}
		else if(stateSortmap.containsKey(index)){
			type="state";
		}
		else{
			System.out.println("There are no indices defined for the given data or there are no records having this field :"+index);
		}

	}
	public static void delete(Integer index){
		try{
			//if index is defined for the ID
			if(idSortmap.containsKey(index)){
				Integer offsets= idSortmap.get(index);
				RandomAccessFile file = new RandomAccessFile("C:\\Text Books\\DB\\project2\\data.db", "rw");

				int count=0;
				file.seek(offsets);
				System.out.println("Details of the deleted record:");
				while(true){
					char c=(char) file.readByte();
					if(c =='|'){
						break;
					}
					System.out.print(c);
					//filewr.writeChar('0');
					offsets++;
					count++;
				}	
				System.out.println();
				file.close();
				String replace ="";
				RandomAccessFile filewr = new RandomAccessFile("C:\\Text Books\\DB\\project2\\data.db", "rw");
				offsets=idSortmap.get(index);
				filewr.seek(offsets);
				//replacing the db file with 0's in place of the record 
				for(int i=0;i<count;i++)
					replace+="0";
				filewr.write(replace.getBytes());
				filewr.close();
				//removing index from the map
				Integer off=idSortmap.get(index);
				idSortmap.remove(index);
				//removing index from state map
				for( String state : stateSortmap.keySet()){

					ArrayList<Integer> arr=stateSortmap.get(state);

					if(arr.contains(off)){

						arr.remove(off);
						if(arr.isEmpty()){
							stateSortmap.remove(state);
						}else{
							stateSortmap.put(state, arr);
						}
					}


				}
				//removing index from lastname map
				for( String lname : lnameSortmap.keySet()){

					ArrayList<Integer> arr=lnameSortmap.get(lname);
					//if(state.equals("FL")) System.out.println(arr.toString());
					if(arr.contains(off)){

						arr.remove(off);
						if(arr.isEmpty()){
							lnameSortmap.remove(lname);
						}else{
							lnameSortmap.put(lname, arr);
						}
					}


				}
				//rewriting the map back to the index files
				makeindex("id");
				makeindex("state");
				makeindex("lname");

			}
			else {
				System.out.println("There are no indices defined for the given data or there are no records having this field :"+index);
			}
		}catch(IOException io){
			io.printStackTrace();
		}
	}
	public static int count(){
		//gets the number of records in the database
		if(idSortmap.isEmpty()){
			System.out.println("Please make sure first invoke the 'readCSV' method. The records are empty");
			return 0;
		}
		return idSortmap.size();
	}
	public static void insert(String record){
		try{
			String [] s=record.split(",");

			String id=s[0];
			id=id.replaceAll("\",", "");
			id=id.replaceAll("\"", "");
			String lname=s[2];
			lname=lname.replaceAll("\"", "");
			String state=s[7];
			if(state.length()>2){
				state=s[8];
			}
			state=state.replaceAll("\"", "");

			if(idSortmap.containsKey(Integer.parseInt(id))){
				System.out.println("Insert: This ID already exists. Please modify the existing record or enter a new ID");

				return;
			}
			RandomAccessFile file = new RandomAccessFile("C:\\Text Books\\DB\\project2\\data.db", "rw");
			file.seek(finaloffset);
			record=record.replace("\\", "");
			record=record+"|";
			System.out.println("New record inserted:\n"+record);
			file.write(record.getBytes());
			idSortmap.put(Integer.parseInt(id), finaloffset);
			if(stateSortmap.containsKey(state)){
				ArrayList<Integer> list=stateSortmap.get(state);
				list.add(finaloffset);
				stateSortmap.put(state, list);
			}
			else{
				ArrayList<Integer> list= new ArrayList<Integer>();
				list.add(finaloffset);
				stateSortmap.put(state, list);
			}
			if(lnameSortmap.containsKey(lname)){
				ArrayList<Integer> list=lnameSortmap.get(lname);
				list.add(finaloffset);
				lnameSortmap.put(lname, list);
			}
			else{
				ArrayList<Integer> list= new ArrayList<Integer>();
				list.add(finaloffset);
				lnameSortmap.put(lname, list);
			}

			finaloffset+=record.length();
			file.close();
			makeindex("id");
			makeindex("state");
			makeindex("lname");
		}
		catch(IOException io){

		}
	}
	public static void modify(Integer id, String field, String value){
		//checks if the record exists in the database
		if(!idSortmap.containsKey(id)){
			System.out.println("There is no record with the given ID");
		}else{
			try{
				String rec=""; 
				int off=idSortmap.get(id);
				RandomAccessFile file = new RandomAccessFile("C:\\Text Books\\DB\\project2\\data.db", "rw");
				file.seek(off);
				while(true){
					char c=(char) file.readByte();
					if(c == '|'){
						break;
					}
					//System.out.print(c);
					rec+=c;
					off++;

				}
				System.out.println();
				String [] s=rec.split("\",");
				System.out.println(s[7]);
				String oldVal="";
				//modifying the fields
				if(field.equalsIgnoreCase("id")){
					oldVal=s[0];
					s[0]=value;
				}else if(field.equalsIgnoreCase("lname")){
					oldVal=s[2];
					s[2]=value;
				}else if(field.equalsIgnoreCase("state")){

					oldVal=s[7];
					s[7]="\""+value;

				}
				String replace="";
				for(int i=0;i<s.length;i++){
					if(i==s.length-1)
						replace+=s[i];					
					else
						replace+=s[i]+"\",";
				}
				off=idSortmap.get(id);
				System.out.println("replace"+replace);
				file.seek(off);
				file.write(replace.getBytes());
				file.close();
				oldVal=oldVal.replace("\"", "");
				if(field.equalsIgnoreCase("lname")){
					if(oldVal!=value){
						//modifying the map
						ArrayList<Integer> list=lnameSortmap.get(oldVal);
						list.remove(idSortmap.get(id));
						if(list.isEmpty()){
							lnameSortmap.remove(oldVal);
						}
						if(lnameSortmap.containsKey(value)){
							ArrayList<Integer> newList= lnameSortmap.get(value);
							newList.add(idSortmap.get(id));
							lnameSortmap.put(value, newList);
						}else{
							ArrayList<Integer> newList= new ArrayList<Integer>();
							newList.add(idSortmap.get(id));
							lnameSortmap.put(value, newList);
						}
					}
				}else if(field.equalsIgnoreCase("state")){
					if(!oldVal.endsWith(value)){
						//modifying the map
						ArrayList<Integer> list=stateSortmap.get(oldVal);
						list.remove(idSortmap.get(id));
						if(list.isEmpty()){
							stateSortmap.remove(oldVal);
						}
						if(stateSortmap.containsKey(value)){
							//modifying the map
							ArrayList<Integer> newList= stateSortmap.get(value);
							newList.add(idSortmap.get(id));
							stateSortmap.put(value, newList);
						}else{
							ArrayList<Integer> newList= new ArrayList<Integer>();
							newList.add(idSortmap.get(id));
							stateSortmap.put(value, newList);
						}
					}
				}

				//rewriting the map back to the index files
				makeindex("state");
				makeindex("lname");
			}catch(IOException io){

			}

		}
	}
	//driver program
	public static void main(String[] args) {
		//To perform any action, the first method invoked should be readCsv. It should be invoked only once to load the data from CSV
		readCsv();		
		select("TX");
		//delete(500);
		System.out.println("The number of records: "+count());
		//To insert records place an escape character before every 
		String rec="\"599\",\"James\",\"Butt\",\"Benton, John B Jr\",\"6649 N Blue Gum St\",\"New Orleans\",\"Orleans\",\"LA\",70116,\"504-621-8927\",\"504-845-1427\",\"jbutt@gmail.com\",\"http://www.bentonjohnbjr.com\"";
		insert(rec);
		System.out.println("The number of records: "+count());

		modify(599,"state","TX");
		select("Butt");
	}

}
