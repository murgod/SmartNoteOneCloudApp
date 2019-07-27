

#listing all the stacks
aws cloudformation list-stacks --stack-status-filter CREATE_COMPLETE||CREATE_IN_PROGRESS||REVIEW_IN_PROGRESS||DELETE_IN_PROGRESS||DELETE_FAILED||UPDATE_IN_PROGRESS||DELETE_COMPLETE

echo "Enter the stack you want to delete"
read Stack_Name

aws cloudformation delete-stack --stack-name $Stack_Name

Success=$(aws cloudformation wait stack-delete-complete --stack-name $Stack_Name)
if [[ -z "$Success" ]]
then
  echo "$Stack_Name stack is deleted successfully"
else
  echo "Deletion of $Stack_Name stack failed."
  echo "$Success"
  exit 1
fi



