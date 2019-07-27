aws cloudformation list-stacks --stack-status-filter CREATE_COMPLETE||CREATE_IN_PROGRESS||REVIEW_IN_PROGRESS||DELETE_IN_PROGRESS||DELETE_FAILED||UPDATE_IN_PROGRESS||DELETE_COMPLETE

echo "Enter the stack name to be deleted"
read s_name

StackList=$(aws cloudformation list-stack-resources --stack-name $s_name)

if [ -z $StackList ] ; then
  echo "$Stack_Name Stack does not exist"
  exit 1
fi

#echo "$StackList"

aws cloudformation delete-stack --stack-name $s_name
aws cloudformation wait stack-delete-complete --stack-name $s_name

echo "$Stack_Name Stack is deleted successfully"
