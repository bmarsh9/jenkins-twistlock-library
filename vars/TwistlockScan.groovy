def call(Map options) {
    def imageName = options.get("imageName",false)
    def humanize = options.get("humanize",true)
    def outputFile = options.get("outputFile","prisma-cloud-scan-results.json")
    def message_output = "None"

    // Scan image
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

        def dataMap = prismaOutput[0]["entityInfo"]["vulnerabilityDistribution"]

        // Get url of report in jenkins
        dataMap.jenkinsReportUrl = "${BUILD_URL}imageVulnerabilities".toString()

        if (humanize) {
            message_output = "[TWISTLOCK] Total vulns: ${dataMap.total} | Critical: ${dataMap.critical} | High: ${dataMap.high} | Medium: ${dataMap.medium} | Low: ${dataMap.low} | Link to Report: ${dataMap.jenkinsReportUrl}"
        } else {
            message_output = dataMap.toString()
        }
        return "${message_output}"
    } else {
        return "[WARNING] Prisma Cloud file: ${outputFile} does not exist. Scan likely failed."
    }
}

