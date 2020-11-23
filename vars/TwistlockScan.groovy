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

    prismaCloudPublish resultsFilePattern: 'prisma-cloud-scan-results.json'

    echo "${BUILD_URL}imageVulnerabilities"
}

