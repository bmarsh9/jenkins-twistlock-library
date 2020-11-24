//import groovy.json.*

def call(Map options) {
    def imageName = options.get("imageName",false)
    def humanize = options.get("humanize",true)
    def outputFile = options.get("outputFile","prisma-cloud-scan-results.json")
    def verbose = options.get("verbose",true)
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

    // TODO: Send to Prisma for IaC eval or run regex on the file
    //def k8_file = "${env:WORKSPACE}/k8s/base/deployment.yaml"
    //if (fileExists("${k8_file}")) {
      //def k8_yaml = readYaml file: "${k8_file}"
      //def k8_json = new JsonBuilder(k8_yaml).toPrettyString()
    //}

    // Parse Prisma Cloud image scan
    if (fileExists("${env:WORKSPACE}/${outputFile}")) {
        sh "chmod 777 ${outputFile}"

        // Get prisma results json file in workspace
        def prismaOutput = readJSON file: "${env:WORKSPACE}/${outputFile}"

        // Publish results to dash
        prismaCloudPublish resultsFilePattern: "${outputFile}"

        def dataMap = prismaOutput[0]["entityInfo"]["vulnerabilityDistribution"]

        // Get url of report in jenkins and build status
        dataMap.jenkinsReportUrl = "${BUILD_URL}imageVulnerabilities".toString()
        dataMap.pass = prismaOutput[0]["pass"].toString()

        if (humanize) {
            message_output = "[TWISTLOCK:INFO] Pass: ${dataMap.pass} | Total vulns: ${dataMap.total} | Critical: ${dataMap.critical} | High: ${dataMap.high} | Medium: ${dataMap.medium} | Low: ${dataMap.low} | Link to Report: ${dataMap.jenkinsReportUrl}"
        } else {
            message_output = dataMap.toString()
        }
        if (verbose) {
            echo "${message_output}"
        }
        pullRequest.comment("test test")
        return "${message_output}"
    } else {
        message_output = "[TWISTLOCK:WARNING] Prisma Cloud file: ${outputFile} does not exist. Scan likely failed."
        if (verbose) {
            echo "${message_output}"
        }
        return "${message_output}"

    }
}

