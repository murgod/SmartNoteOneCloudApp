#!/bin/bash
#-----------------------------------------------------------------------------------
#    Shell Script to Delete The VPC
#-----------------------------------------------------------------------------------
#
#
# DESCRIPTION
# 1) Deletes the VPC created
# 2) Deletes the Subnet created in the VPC
# 3) Deletes the Internet Gateway created
# 4) Deletes the Route Table
# 5) Deletes the Custom Security Group

region="us-east-1"
security_Group="$1-csye6225-security_group"

if [ $# -eq 0 ]; then
	echo " PLEASE PASS <STACK_NAME> as parameter "
	exit 1
fi


# Getting VPC Name
echo -e "\n"
echo "-----GETTING THE VPC NAME-----"
vpc="$1-csye6225-vpc-1"
vpcName=$(aws ec2 describe-vpcs \
	--query "Vpcs[?Tags[?Key=='Name']|[?Value=='$vpc']].Tags[0].Value" \
	--output text)
echo $vpcName

# Getting VPC ID
echo -e "\n"
echo "-----GETTING VPC ID------"
vpc_Id=$(aws ec2 describe-vpcs \
	--query 'Vpcs[*].{VpcId:VpcId}' \
	--filters Name=is-default,Values=false \
	--output text \
 	--region $region)
echo $vpc_Id

# Getting Internet-Gateway-Id
echo -e "\n"
echo "------GETTING INTERNET GATEWAY ID------"
internetGateway_Id=$(aws ec2 describe-internet-gateways \
 	--query 'InternetGateways[*].{InternetGatewayId:InternetGatewayId}' \
 	--filters "Name=attachment.vpc-id,Values=$vpc_Id" \
 	--output text)
echo $internetGateway_Id

# Getting Route-Table-Id
echo -e "\n"
echo "-----GETTING ROUTE-TABLE ID-----"
route_Table_Id=$(aws ec2 describe-route-tables \
	--filters "Name=vpc-id,Values=$vpc_Id" "Name=association.main, Values=false" \
	--query 'RouteTables[*].{RouteTableId:RouteTableId}' \
	--output text)

route_Table_Id1=${route_Table_Id}

echo "First Route-Table ID: '$route_Table_Id1'"

# Disassociates the Public Subnets with Route Table
echo -e "\n"
echo "-----DIASSOCIATING SUBNET AND ROUTE TABLE-----"
aws ec2 describe-route-tables \
	--query 'RouteTables[*].Associations[].{RouteTableAssociationId:RouteTableAssociationId}' \
	--route-table-id $route_Table_Id1 \
	--output text|while read var_Associate; do aws ec2 disassociate-route-table --association-id $var_Associate; done
echo "DIASSOCAITED SUBNET AND ROUTE TABLE"

echo -e "\n"
echo "-----DELETING SUBNETS-----"
while
sub=$(aws ec2 describe-subnets \
	--filters Name=vpc-id,Values=$vpc_Id \
	--query 'Subnets[*].SubnetId' \
	--output text)
[[ ! -z $sub ]]
do
        var1=$(echo $sub | cut -f1 -d" ")
        echo $var1 is deleted
        aws ec2 delete-subnet --subnet-id $var1
done
echo "DELETED SUBNETS"

# Detach Internet Gateway
echo -e "\n"
echo "-----DETACHING INTERNET GATEWAY-----"
aws ec2 detach-internet-gateway \
	--internet-gateway-id $internetGateway_Id \
	--vpc-id $vpc_Id
echo "DETACHED INTERNET GATEWAY"

# Delete Internet Gateway
echo -e "\n"
echo "-----DELETING INTERNET GATEWAY-----"
aws ec2 delete-internet-gateway \
	--internet-gateway-id $internetGateway_Id
echo "DELETED INTERNET GATEWAY"

# Retrieving main route table
echo -e "\n"
echo "-----GETTING ROUTE TABLE-----"
main_Route_Table_Id=$(aws ec2 describe-route-tables \
	--query "RouteTables[?VpcId=='$vpc_Id']|[?Associations[?Main!=true]].RouteTableId" \
	--output text)

echo "id = $main_Route_Table_Id"

#Delete Route-Table
echo -e "\n"
echo "-----DELETING ROUTE-TABLE-----"
for i in $route_Table_Id
do
	echo "Start------ $main_Route_Table_Id"
	if [[ $i != $main_Route_Table_Id ]]; then
		aws ec2 delete-route-table --route-table-id $i
		echo $i
	fi
	echo "stop----- $main_Route_Table_Id"
done
echo "DELETED ROUTE TABLE"

# Getting Custom Security Group
echo -e "\n"
echo "------GETTIGN CUSTOM SECURITY GROUP-----"
securityGroupId=$(aws ec2 describe-security-groups \
	--query 'SecurityGroups[*].{GroupId:GroupId}' \
	--filters "Name=vpc-id,Values=$vpc_Id" "Name=group-name, Values=$security_Group" \
	--output text \
 	--region $region)
echo $securityGroupId

# Delete Custom Security Group
echo -e "\n"
echo "------DELETING Custom Security Group-----"
aws ec2 delete-security-group --group-id $securityGroupId
echo "DELETED CUSTOM SECURITY GROUP"


#Delete vpc
echo -e "\n"
echo "-----DELETING VPC-----"
aws ec2 delete-vpc --vpc-id $vpc_Id
echo "DELETED VPC"
