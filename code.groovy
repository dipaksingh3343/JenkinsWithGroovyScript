job("task6-job1") {
  description("This job will pull the github repo auomatically when any developer push github repo related to #GroovyCode.")
  scm {
    github('Rahulgithub-code/JenkinsWithGroovyScript','master')
      }
  
  steps {
    shell('sudo cp -vrf * /root/task6')
  }
  triggers {
        		upstream('Groovy_Seed_GitHub', 'SUCCESS')
}

  
  triggers {
    scm('* * * * *')
  }
  
  wrappers {
    preBuildCleanup()
}
}

job("task6-job2"){
  description("This job create Kubernetes os  according code (If code contain html syntax than it will create html interpreter or if it has php code than it create php interpreter)")
  steps {
    shell('''if sudo kubectl get pods | grep html
    		then
    	     		sudo kubectl run apply --image rahulwithdocker/httpd-server:v1 -l app=html
             		sudo kubectl expose pods html --type=NodePort --port=80 
		else
		    	sudo kubectl run html --image rahulwithdocker/httpd-server:v1 -l app=html
             		sudo kubectl expose pods html --type=NodePort --port=80 
	fi		
			
    ''')
  }
  
  triggers {
        		upstream('task6-job1', 'SUCCESS')
  }
}

job("task6-job3")
{
  description("Testing env")
  steps{
    shell('''
status=$(curl -o /dev/null -s -w "%{http_code}" http://192.168.99.101:30915)
if [[ $status == 000 ]]
then
    echo "Running"
    exit 0
else
     exit 1
fi
     ''')
  }
  
  triggers {
        upstream('task6-job2', 'SUCCESS')
  }
  
  publishers {
        extendedEmail {
            recipientList('rahulkr.mits@gmail.com')
            defaultSubject('Job status')
          	attachBuildLog(attachBuildLog = true)
            defaultContent('Status Report')
            contentType('text/html')
            triggers {
                always {
                    subject('build Status')
                    content('Body')
                    sendTo {
                        developers()
                        recipientList()
                    }
		}
	    }
	}
    }
}
