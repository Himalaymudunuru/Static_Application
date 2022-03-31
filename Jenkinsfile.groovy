pipeline {
    agent any

    stages {
        stage('git clone') {
            steps {
                git branch: 'main', url: 'https://github.com/Himalaymudunuru/Static_Application.git'
            }
        }
        
        stage('docker build') {
            steps {
               withCredentials([string(credentialsId: 'Dokcer_creds', variable: 'Password')]) {
                   
                   sh '''docker login -u=hima12345 -p="${Password}"
                         docker build -t hima12345/nginx .
                         docker push hima12345/nginx
                   
                   '''
    
                 }
            }
        }
        
         stage('deploy') {
            steps {
                withCredentials([kubeconfigContent(credentialsId: 'Kubeconfig', variable: 'KUBECONFIG_CONTENT')]) {
                  sh 'echo "${KUBECONFIG_CONTENT}" >> admin.conf  && kubectl --kubeconfig ./admin.conf get nodes  && ls -l && pwd && cat deployment.yaml && kubectl apply -f ./deployment.yaml --validate=false --kubeconfig ./admin.conf  && kubectl apply -f ./service.yaml --validate=false --kubeconfig ./admin.conf  '
                  
               }
            }
        }
    }
}
