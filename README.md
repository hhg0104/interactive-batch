## Build, Execution of JAR
1. Build a code
   1. ./gradlew clean build
2. Run the jar
   1. java -jar MobilityOperationTool.jar

## Operation Procedure

### - Common Procedure
1. <b>Input the operation ID</b>: 
   1. Choose an operation ID from the list shown
2. <b>Input RTRV ticket link</b>: 
   1. Input the target operation RTRV ticket link

### - Data update batch
> This operation updates the booking data comparing excel and DB data.

#### Preparation

1. Prepare the excel files from the issue ticket.
   1. Confirm the each excel files has "予約番号" column, it doesn't exist, need to create and input booking id.
   2. Check if the point usage data from the issue ticket, if the point usage data exists, need to create "ポイント利用" column and input point into excel

#### Procedure
1. Input the excel file path (multiple files input):
   1. Input the excel files to compare with DB data. It can be full path and relative path.
2. Input DB info
   1. Input the DB info to fetch booking data
   2. ex.
      1. Input DB server host ({host}:{port}/{db}): <i><b>localhost:3306/testdb</b></i>
      2. Input DB user: <i><b>root</b></i>
      3. Input DB password: <i><b>root</b></i>
3. 1 SQL query file and 1 diff data markdown file will be created. The RTRV ticket name will be used for the name of the SQL query file.
   1. ex.
      1. <i><b>ISSUE-14526.sql</b></i>
      2. <i><b>ISSUE-14526_target_data.md</b></i>

