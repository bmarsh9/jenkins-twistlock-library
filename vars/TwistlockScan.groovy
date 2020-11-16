import org.example.Constants

def call(Map config=[:]) {
    node {
        stage("Scan Image") {
            steps {
                prismaCloudScanImage ca: '',
                cert: '',
                dockerAddress: 'unix:///var/run/docker.sock',
                image: config.image,
                key: '',
                logLevel: 'debug',
                podmanPath: '',
                project: '',
                resultsFile: Constants.TWISTLOCK_FILE_OUTPUT,
                ignoreImageBuildTime:true
            }
        }
        stage('Generate Report') {
            steps {
              sh 'echo generating report'
            }
        }
    }
}
