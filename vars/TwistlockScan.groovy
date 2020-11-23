def call(Map options) {
    def imageName = options.get("imageName",false)
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

    prismaCloudPublish resultsFilePattern: '${outputFile}'

    //def "${BUILD_URL}imageVulnerabilities"
    def prismaOutput = readJSON file: "${env:WORKSPACE}/${outputFile}"

    def dataMap = prismaOutput[0]["entityInfo"]["vulnerabilityDistribution"]
    //dataMap.jenkinsReportUrl = "${BUILD_URL}imageVulnerabilities"

    echo dataMap

    //echo prismaOutput[0]["entityInfo"]["vulnerabilityDistribution"].toString()

    //def dataMap = [:]
    
}

