import groovy.io.FileType

class FileScanner {

	public static String proglibDir =  "D:\\infolease\\il\\il.10.1\\proglib"
	public static String csvFilePath = "D:\\Users\\ranjeet1\\Documents\\Tax Integration - System number enhancement\\Customers with System_Company Numbers.txt"
	public static String outputDir = "D:\\Users\\ranjeet1\\Documents\\Tax Integration - System number enhancement\\output"
	public static String ln = System.getProperty('line.separator')

	public static Set programSet = new HashSet ([
		"CREATE.OPEN.ITEM.RECORD",
		"CREATE.ASSET.OPEN.ITEMS",
		"CREATE.OPEN.ITEM.TAX.FIELDS",
		"SET.TAX.AMTS",
		"CALC.MAX.TAX",
		"CALC.COUNTY.MAX.TAX",
		"CALC.CITY.MAX.TAX",
		"DETERMINE.MAX.PAYOFF.RECORDS",
		"WEBIL.BUYOUT.MISC.RECALC.TAX",
		"BUYOUT.00.CALC.ITC",
		"BUYOUT.CALC.LC.TAXES",
		"BUYOUT.CALC.MISC",
		"BUYOUT.CALC.SALES.TAX",
		"API.CREATE.AUTO.QUOTE",
		"BUYOUT.GET.TAX.RATES",
		"CALC.PREPAYMENT.PENALTY.FEES",
		"BUYOUT.MISC.01",
		"BUYOUT.MISC.02",
		"CALC.PAYOFF.INVD.TAXES",
		"CALC.FUTURE.MISC",
		"WEBIL.BUYOUT.MISC.RECALC.TAX",
		"DISP.12",
		"RECALC.CONTRACT.ASSETS",
		"INSUR.BILL.MISC.TAX",
		"CMAINT.OI.CHRG.TYPE.WINDOW",
		"LATE.CHARGES",
		"CREATE.MISC.TAXES"
	])

	static void readCsvFile(){
		def csvFile = new File(csvFilePath)
		int lineNum = 0
		csvFile.eachLine{ line ->
			lineNum++
			def currentLine = line.trim()
			if(currentLine){
				def lineContents = currentLine.split("\\|")
				def companyName = lineContents[0].trim().replace(",", "comma").replace(".", "dot").replace("/", "by")
				def systemNumber = lineContents[2].split("-")[1].trim()
				def country= lineContents[1].trim()
				def countryNumber = lineContents[3].trim()
				def logText = "Processing line $lineNum...$ln$ln Company Name= $companyName $ln Country= $country $ln System Number= $systemNumber $ln Country Number= $countryNumber $ln$ln"
				println logText
				log(logText)
				scanFiles(systemNumber, companyName)
			}
		}
	}

	public static void log(String content){
		writeToFile("script.log.txt", content)
	}

	public static void writeToFile(String fileName, String data, String header = null){
		def file= new File(outputDir, fileName)
		if(header){
			file << "${header} $ln"
		}
		file << "${data} $ln$ln"
	}

	public static void scanFiles(String systemNumber, String companyName, String rootDirectory = proglibDir) {
		def dir = new File(rootDirectory)
		def list = []
		int total = 0

		def outFileName = systemNumber+" - "+companyName+".txt"

		dir.eachFileRecurse(FileType.FILES) { file ->
			if(!file.name.startsWith("_")){
				def map = [:]
				boolean matchFound = false
				int numberOfOccurence = 0

				file.eachLine { line ->
					if(line =~/SYSTEM.NUMBER\s*=\s*$systemNumber/){
						matchFound = true
						numberOfOccurence++
					}
				}

				if(matchFound){
					map["name"] = file
					map["systemNumberOccurence"] = numberOfOccurence

					if(programSet.contains(file.name)){
						if(!outFileName.contains("RefactorFor")){
							outFileName = "RefactorFor-"+outFileName
						}
					} else {
						outFileName = outFileName.replace("RefactorFor-", "")
					}
					writeToFile(outFileName, map.toMapString())
					list << map
					log numberOfOccurence+ " match(s) found in "+ file +"for SYSTEM.NUMBER = "+ systemNumber
				}
				total ++
			}
		}
		def finalLog =  "$ln +++++++++++++++++++++++++++++++++++++++++++++++++++ $ln"
		finalLog += "$ln Total files processed in directory \"$rootDirectory\" = "+ total + ln
		finalLog += "$ln Total possible match found= "+ list.size() + ln
		finalLog += "$ln +++++++++++++++++++++Deatils++++++++++++++++++++++++++++++ $ln"
		finalLog +=  list.toString()
		finalLog +=  "$ln +++++++++++++++++++++++++++++++++++++++++++++++++++ $ln"

		log(finalLog)
	}


	public static void main(String[] args){
		readCsvFile()
	}
}






