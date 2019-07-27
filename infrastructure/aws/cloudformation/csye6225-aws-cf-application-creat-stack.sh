
aws cloudformation list-stacks --stack-status-filter CREATE_COMPLETE||CREATE_IN_PROGRESS||REVIEW_IN_PROGRESS||DELETE_IN_PROGRESS||DELETE_FAILED||UPDATE_IN_PROGRESS||DELETE_COMPLETE

echo "Enter the stack you want to create"
read Stack_Name



echo "Displaying all keys!"
aws ec2 describe-key-pairs
echo -e "\n"
echo "Choose 1 Key which you want to use!"
read KEY_CHOSEN

echo "Displaying AMI!"
aws ec2 describe-images --owners self --query 'Images[*].{ID:ImageId}'
echo -e "\n"
echo "Enter AMI ID"
read amiId

echo "Enter Bucket Name for Attachment Upload"
read uploadbucket

echo "Enter Bucket Name for codedeploy Upload"
read codedeploybucket

echo "Enter from address for password Reset Link"
read fromaddr

echo "Enter Account number for AWS"
read accountno

echo "Enter your Domain name"
read domain_name

echo "Enter ARN for your first ssl certificate"
read cert_arn

echo "Enter ARN for your second ssl certificate"
read cert_arn_2


getStackStatus() {
	aws cloudformation describe-stacks \
		--stack-name $Stack_Name \
		--query Stacks[].StackStatus \
		--output text
}

waitForState() {
	local status

	status=$(getStackStatus $1)

	while [[ "$status" != "$2" ]]; do

		echo "Waiting for stack $1 to obtain status $2 - Current status: $status"
		# If the status is not one of the "_IN_PROGRESS" status' then consider

		# this an error
		if [[ "$status" != *"_IN_PROGRESS"* ]]; then
			exitWithErrorMessage "Unexpected status '$status'"
		fi

		status=$(getStackStatus $1)
		sleep 30

	done

	echo "Stack $1 obtained $2 status"

}


exitWithErrorMessage() {
	echo "ERROR: $1"
	exit 1
}
#-------------------------------------------------------------------------------
# Returns a file URL for the given path
#
# Args:
# $1  Path
#-------------------------------------------------------------------------------

dir_var=$(pwd)
# echo "Current Directory is '$dir_var'"
file_dir_var="file://$dir_var/csye6225-cf-auto-scaling-application.json"

#Create Stack

aws cloudformation create-stack \
	--stack-name $Stack_Name  \
	--template-body $file_dir_var \
	--parameters ParameterKey="keyname",ParameterValue=$KEY_CHOSEN ParameterKey="AmiId",ParameterValue=$amiId ParameterKey="NameTag",ParameterValue="ec2" ParameterKey="webappbucket",ParameterValue="$uploadbucket" ParameterKey="codedeploybucket",ParameterValue="$codedeploybucket" ParameterKey="fromaddress",ParameterValue="$fromaddr" ParameterKey="Accountno",ParameterValue="$accountno" ParameterKey="DomainName",ParameterValue="$domain_name" ParameterKey="CertificateARN",ParameterValue="$cert_arn" ParameterKey="CertificateARN2",ParameterValue="$cert_arn_2" \
	--disable-rollback \
	--capabilities CAPABILITY_NAMED_IAM



if ! [ "$?" = "0" ]; then

	exitWithErrorMessage "Cannot create stack ${Stack_Name}!"

fi

#Wait for completion
waitForState ${Stack_Name} "CREATE_COMPLETE"
