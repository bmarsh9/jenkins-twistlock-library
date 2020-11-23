def call(Map options) {
    def imageName = options.get("imageName",false)
    def humanize = options.get("humanize",true)
    def outputFile = options.get("outputFile","prisma-cloud-scan-results.json")
    def message_output = "None"

    prismaCloudScanImage ca: '',
      cert: '',
      dockerAddress: 'unix:///var/run/docker.sock',
      image: imageName,
      key: '',
      logLevel: 'debug',
      podmanPath: '',
      project: '',
      resultsFile: outputFile,
      ignoreImageBuildTime:true

    prismaCloudPublish resultsFilePattern: "${outputFile}"

    // Get URL of report in Jenkins
    def reportUrl = "${BUILD_URL}imageVulnerabilities"

    // Get prisma results json file in workspace
    def prismaOutput = readJSON file: "${env:WORKSPACE}/${outputFile}"

    // Create dict off vulnerability data
    def dataMap = prismaOutput[0]["entityInfo"]["vulnerabilityDistribution"]
    dataMap.jenkinsReportUrl = reportUrl.toString()

    if (humanize) {
        echo "human"
        message_output = "Total vulns: ${dataMap.total} | Critical: ${dataMap.critical} | High: ${dataMap.high} | Medium: ${dataMap.medium} | Low: ${dataMap.low} | Link to Report: ${dataMap.jenkinsReportUrl}"
        echo "${message_output}"
        echo "after"
    } else {
        echo "json output"
        message_output = dataMap.toString()
    }
    echo "${message_output}"

    
    //echo dataMap.toString()

}

