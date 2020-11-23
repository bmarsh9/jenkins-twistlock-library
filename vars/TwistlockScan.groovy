def call(Map options) {
    def imageName = options.get("imageName",false)
    def humanize = options.get("humanize",true)
    def outputFile = options.get("outputFile","prisma-cloud-scan-results.json")

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
        def message = "Total vulns: ${dataMap.total} | Critical: ${datamap.critical} | High: ${datamap.high} | Medium: ${datamap.medium} | Low: ${datamap.low} | Link to Report: ${datamap.jenkinsReportUrl}"
    } else {
        def message = dataMap.toString()
    }
    echo message

    
    //echo dataMap.toString()

}

