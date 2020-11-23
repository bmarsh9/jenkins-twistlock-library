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

    // Get prisma results json file in workspace
    def prismaOutput = readJSON file: "${env:WORKSPACE}/${outputFile}"

    if (fileExists("${env:WORKSPACE}/${outputFile}")) {

        // Publish results to dash
        prismaCloudPublish resultsFilePattern: "${outputFile}"

        // Get URL of report in Jenkins
        def reportUrl = "${BUILD_URL}imageVulnerabilities"

        // Create dict off vulnerability data
        def dataMap = prismaOutput[0]["entityInfo"]["vulnerabilityDistribution"]
        dataMap.jenkinsReportUrl = reportUrl.toString()

        if (humanize) {
            message_output = """[Twistlock] Total vulns: ${dataMap.total}
                | Critical: ${dataMap.critical}
                |High: ${dataMap.high}
                |Medium: ${dataMap.medium}
                |Low: ${dataMap.low}
                |Link to Report: ${dataMap.jenkinsReportUrl}""".stripMargin()
        } else {
            message_output = dataMap.toString()
        }
        echo "${message_output}"
    } else {
        echo "[WARNING] Prisma Cloud file: ${outputFile} does not exist. Scan likely failed."
    }

    
    //echo dataMap.toString()

}

