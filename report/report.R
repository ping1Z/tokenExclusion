require(ggplot2) # for data visualization

createReportDF <- function(numOfNode, mode, exeTime,resTime, thp, mc){
  str(numOfNode)
  df <- data.frame(NumOfNode=c(numOfNode),
                        Mode=c(mode),
                        #ExecutionTime=c(exeTime), 
                        ResponseTime=c(resTime),
                        Throughput=c(thp), 
                        MsgComplexity=c(mc), 
                        stringsAsFactors=FALSE)
  return(df)
  
}

analysisReport <- function(numOfNode, mode, count){
  
  df_exe= NULL
  for (i in 1:count) {
    exePath = sprintf('./report/%d_%d_%dreport_executionTime.txt',numOfNode, mode,i)
    df_tmp = read.csv(exePath, stringsAsFactors = FALSE)
    df_exe = rbind(df_exe,df_tmp)
  }
  df_exe_mean = mean(df_exe[["TIME"]])
  str(df_exe_mean)
  
  df_res= NULL
  for (i in 1:count) {
    resPath = sprintf('./report/%d_%d_%dreport_responseTime.txt',numOfNode, mode,i)
    df_tmp = read.csv(resPath, stringsAsFactors = FALSE)
    df_res = rbind(df_res,df_tmp)
  }
  df_res_mean = mean(df_res[["TIME"]])
  str(df_res_mean)
  
  
  df_thp= NULL
  for (i in 1:count) {
    thpPath = sprintf('./report/%d_%d_%dreport_throughput.txt',numOfNode, mode,i)
    df_tmp = read.csv(thpPath, stringsAsFactors = FALSE)
    df_thp = rbind(df_thp,df_tmp)
  }
  df_thp_mean = numOfNode*100/mean(df_thp[["TIME"]])*1000
  str(df_thp_mean)
  
  df_mc= 0
  for (i in 1:count) {
    mcPath = sprintf('./report/%d_%d_%dreport_MessageComplexity.txt',numOfNode, mode,i)
    df_tmp = read.csv(mcPath, stringsAsFactors = FALSE)
    df_mc =  df_mc + nrow(df_tmp);
  }
  df_mc_mean = df_mc/count/100/numOfNode
  str(df_mc_mean)
  mode <- if(mode==1) "Greedy" else "Non-greedy"
  return(createReportDF(numOfNode, mode, df_exe_mean, df_res_mean, df_thp_mean, df_mc_mean))
}


df_data <- data.frame(NumOfNode=integer(),
                      Mode=integer(),
                 #ExecutionTime=double(), 
                 ResponseTime=double(),
                 Throughput=double(), 
                 MsgComplexity=double(), 
                 stringsAsFactors=FALSE) 



numOfNode = c(5,10,20)

for(i in numOfNode){
  row = analysisReport(i, 1, 3);
  
  df_data = rbind(df_data,row)
}

for(i in numOfNode){
  row = analysisReport(i, 2, 3);
  
  df_data = rbind(df_data,row)
}

str(df_data)

# ResponseTime
ggplot(df_data, aes(fill=as.factor(df_data$Mode), y=df_data$ResponseTime, x=df_data$NumOfNode)) + 
  geom_bar(position="dodge", stat="identity") + labs(x = "Num of Nodes", y = "Response Time per Request", fill="Mode")

# ExecutionTime
#ggplot(df_data, aes(fill=as.factor(df_data$Mode), y=df_data$ExecutionTime, x=df_data$NumOfNode)) + 
#  geom_bar(position="dodge", stat="identity") + labs(x = "Num of Nodes", y = "Execution Time per Request", fill="Mode")

# Throughput
ggplot(df_data, aes(fill=as.factor(df_data$Mode), y=df_data$Throughput, x=df_data$NumOfNode)) + 
  geom_bar(position="dodge", stat="identity") + labs(x = "Num of Nodes", y = "Throughput per Second", fill="Mode")

# Message Complexity
ggplot(df_data, aes(fill=as.factor(df_data$Mode), y=df_data$MsgComplexity, x=df_data$NumOfNode)) + 
  geom_bar(position="dodge", stat="identity") + labs(x = "Num of Nodes", y = "Message Complexity per Request", fill="Mode")


# Implementation Correctness
df_ts = read.csv('./report/20_1_1report_timestamp_evaluation.txt', stringsAsFactors = FALSE)

# Uniform color
ggplot(df_ts, aes(x=df_ts$TIMESTAMP)) + 
  geom_histogram(binwidth = 0.2, color=rgb(0.26, 0.95, 0.87), fill=rgb(0.2,0.7,0.1,0.4) ) +
  labs(x = "Timstamp", y = "Count")
