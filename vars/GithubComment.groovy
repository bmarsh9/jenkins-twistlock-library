def call(Map options) {
    def comment = options.get("comment","Please check job in Jenkins")
    def repository_url = scm.userRemoteConfigs[0].url
    def repository_name = repository_url.replace("git@github.com:","").replace(".git","")

    echo "${comment}"
    echo "${repository_url}"
    echo "${repository_name}"

    //withCredentials([string(credentialsId: '<YOUR-TOKEN-ID>', variable: 'GITHUB_TOKEN')]) {
    //    sh "curl -s -H \"Authorization: token ${GITHUB_TOKEN}\" -X POST -d '{\"body\": \"${comment}\"}' \"https://api.github.com/repos/${repository_name}/issues/${ghprbPullId}/comments\""
    //}
}
