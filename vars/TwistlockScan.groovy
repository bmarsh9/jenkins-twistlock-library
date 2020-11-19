//import org.example.Constants

def call(Map options) {
    def imageName = options.get("imageName",false)
    def outputFile = options.get("outputFile","prisma-cloud-scan-results.json")
    node {
        stage("Scan Image") {
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
        }
        stage ("Report") {
          sh 'testing the output'
        }
    }
}

