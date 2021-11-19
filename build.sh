rm -vf codebuild-docker.zip
zip -r codebuild-docker.zip *
aws s3 cp codebuild-docker.zip s3://crashnight-bucket/