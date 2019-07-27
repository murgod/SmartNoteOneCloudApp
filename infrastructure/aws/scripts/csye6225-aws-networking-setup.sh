#!/bin/bash
#-----------------------------------------------------------------------------------
#    Shell Script create VPC on AWS EC2 using aws-cli commands
#-----------------------------------------------------------------------------------
#
#----------------------------------------------------------------------------------
# What this SHELL script do ?
#
#1.Create a Virtual Private Cloud (VPC).
#2.Create subnets in your VPC. You must create 3 subnets, each in different availability zone in the same region under same VPC.
#3.Create Internet Gateway resource.
#4.Attach the Internet Gateway to the created VPC.
#5.Create a public Route Table. Attach all subnets created above to the route table.
#6.Create a public route in the public route table created above with destination CIDR block 0.0.0.0/0 and internet gateway creted above as the target.
#7.Modify the default security group for your VPC to remove existing rules and add new rules to only allow TCP traffic on port 22 and 80 from anywhere.
#------------------------------------------------------------------------------------


if [ $# -eq 0 ]; then
echo "Scrip required one command line argument. Please pass <stack_name>"
echo "Ex : sh csye6225-aws-networking-setup.sh <stack_name>"
exit 1
fi

REGION="us-east-1"
vpcName="$1-csye6225-vpc-1"
internetGatewayName="$1-csye6225-InternetGateway-1"
vpc_cidr="10.0.0.0/16"
publicRouteTableName="$1-csye6225-public-route-table"
ZONE1="us-east-1a"
ZONE2="us-east-1b"
ZONE3="us-east-1c"
PublicSubnet1="$1-csye-public-subnet-1"
PublicSubnet2="$1-csye-public-subnet-2"
PublicSubnet3="$1-csye-public-subnet-3"
default_ip="0.0.0.0/0"
security_group="$1-csye6225-security_group"



#--------------------------------------------------------------------------------------------------
# Step1 : Create a Virtual Private Cloud (VPC)
#-------------------------------------------------------------------------------------------------
echo -e "\n"
echo "--------------------------------------------------------------------------------------------"
echo "Step1 : Create a Virtual Private Cloud (VPC)"
echo "--------------------------------------------------------------------------------------------"
VPC_ID=$(aws ec2 create-vpc \
  --cidr-block $vpc_cidr \
  --query 'Vpc.{VpcId:VpcId}' \
  --output text \
  --region $REGION)
  #--region $REGION 2>&1)
VPC_CREATE_STATUS=$?
echo "VPC creation status : '$VPC_CREATE_STATUS'"

if [ $VPC_CREATE_STATUS -eq 0 ]; then
  echo " VPC ID '$VPC_ID' Created in '$REGION' region."
else
	echo "##########Error:VPC creation command failed, check command status code!!"
  echo " $VPC_ID "
	exit $VPC_CREATE_STATUS
fi

# Rename VPC using aws create-tags command
echo -e "\n"
echo "Rename VPC using aws create-tags command"
VPC_RENAME=$(aws ec2 create-tags \
  --resources $VPC_ID \
  --tags "Key=Name,Value=$vpcName" \
  --region $REGION 2>&1)
VPC_RENAME_STATUS=$?
if [ $VPC_RENAME_STATUS -eq 0 ]; then
  echo "  VPC ID '$VPC_ID' Renamed/Tagged To '$vpcName'."
else
    echo "##########Error: VPC name tagging command failed check command status code!!"
    echo " $VPC_RENAME "
    exit $VPC_RENAME_STATUS
fi

#--------------------------------------------------------------------------------------------------
#Step2: Create subnets in your VPC. You must create 3 subnets, each in different availability zone in the same region under same VPC.
#--------------------------------------------------------------------------------------------------
echo -e "\n"
echo "-------------------------------------------------------------------------------------------------------------------------------------"
echo "Step2: Create subnets in your VPC. You must create 3 subnets, each in different availability zone in the same region under same VPC."
echo "-------------------------------------------------------------------------------------------------------------------------------------"
echo -e "\n"
echo "Please provide IP Address for subnet 1 in x.x.x.x/x"
read CIDR_BLOCK

#echo "Please provide Availability zones for Public subnet 1"
PUBLIC_SUBNET_1=$(aws ec2 create-subnet \
  --vpc-id $VPC_ID \
  --cidr-block $CIDR_BLOCK \
  --availability-zone $ZONE1 \
  --query 'Subnet.{SubnetId:SubnetId}' \
  --output text \
  --region $REGION)

echo  "Subnet ID '$PUBLIC_SUBNET_1' is created in '$ZONE1'" "Availability Zone."

# Rename Public Subnet 1
echo -e "\n"
echo "RENAME PUBLIC SUBNET 1"
SUBNET_RENAME=$(aws ec2 create-tags \
  --resources $PUBLIC_SUBNET_1 \
  --tags "Key=Name,Value=$PublicSubnet1" 2>&1)
SUBNET_RENAME_STATUS=$?
if [ $SUBNET_RENAME_STATUS -eq 0 ]; then
  echo "Public Subnet ID '$PUBLIC_SUBNET_1' NAMED as '$PublicSubnet1'."
else
    echo "Error:PublicSubnet1 name not added!!"
    echo " $SUBNET_RENAME "
    exit $SUBNET_RENAME_STATUS
fi

echo -e "\n"
echo "Please provide IP Address for Public subnet 2 in x.x.x.x/x"
read CIDR_BLOCK

#echo "Please provide Availability zones for Public subnet 2"
PUBLIC_SUBNET_2=$(aws ec2 create-subnet \
  --vpc-id $VPC_ID \
  --cidr-block $CIDR_BLOCK \
  --availability-zone $ZONE2 \
  --query 'Subnet.{SubnetId:SubnetId}' \
  --output text \
  --region $REGION)

echo  "Subnet ID '$PUBLIC_SUBNET_2' is created in '$ZONE2'" "Availability Zone."

# Rename Public Subnet 2
echo -e "\n"
echo "RENAME PUBLIC SUBNET 2"
SUBNET_RENAME=$(aws ec2 create-tags \
  --resources $PUBLIC_SUBNET_2 \
  --tags "Key=Name,Value=$PublicSubnet2" 2>&1)
SUBNET_RENAME_STATUS=$?
if [ $SUBNET_RENAME_STATUS -eq 0 ]; then
  echo "Public Subnet ID '$PUBLIC_SUBNET_2' NAMED as '$PublicSubnet2'."
else
    echo "Error:PublicSubnet2 name not added!!"
    echo " $SUBNET_RENAME "
    exit $SUBNET_RENAME_STATUS
fi


echo -e "\n"
echo "Please provide IP Address for Public subnet 3 in x.x.x.x/x"
read CIDR_BLOCK

# echo "Please provide Availability zones for Public subnet 3"
PUBLIC_SUBNET_3=$(aws ec2 create-subnet \
  --vpc-id $VPC_ID \
  --cidr-block $CIDR_BLOCK \
  --availability-zone $ZONE3 \
  --query 'Subnet.{SubnetId:SubnetId}' \
  --output text \
  --region $REGION)

echo  "Subnet ID '$PUBLIC_SUBNET_3' is created in '$ZONE3'" "Availability Zone."

# Rename Public Subnet 3
echo -e "\n"
echo "RENAME PUBLIC SUBNET 3"
SUBNET_RENAME=$(aws ec2 create-tags \
  --resources $PUBLIC_SUBNET_3 \
  --tags "Key=Name,Value=$PublicSubnet3" 2>&1)
SUBNET_RENAME_STATUS=$?
if [ $SUBNET_RENAME_STATUS -eq 0 ]; then
  echo "Public Subnet ID '$PUBLIC_SUBNET_3' NAMED as '$PublicSubnet3'."
else
    echo "Error:PublicSubnet3 name not added!!"
    echo " $SUBNET_RENAME "
    exit $SUBNET_RENAME_STATUS
fi

#--------------------------------------------------------------------------------------------------
#Step3:Create Internet Gateway resource.
#-------------------------------------------------------------------------------------------------
echo -e "\n"
echo "--------------------------------------------------------------------------------------------"
echo "Step3.Create Internet Gateway resource."
echo "--------------------------------------------------------------------------------------------"
IGW_ID=$(aws ec2 create-internet-gateway \
  --query 'InternetGateway.{InternetGatewayId:InternetGatewayId}' \
  --output text \
  --region $REGION 2>&1)
IGW_CREATE_STATUS=$?
if [ $IGW_CREATE_STATUS -eq 0 ]; then
  echo "  Internet Gateway ID '$IGW_ID' CREATED."
else
    echo "##########Error:Gateway not created"
    echo " $IGW_ID "
    exit $IGW_CREATE_STATUS
fi

# Rename Internet gateway using aws create-tags command
echo -e "\n"
echo "#3.Create Internet Gateway resource."
IGW_RENAME=$(aws ec2 create-tags \
  --resources $IGW_ID \
  --tags "Key=Name,Value=$internetGatewayName" 2>&1)
IGW_RENAME_STATUS=$?
if [ $IGW_RENAME_STATUS -eq 0 ]; then
  echo "  Internet gateway ID '$IGW_ID' Renamed/Tagged as '$internetGatewayName'."
else
    echo "##########Error :internetGatewayName rename command returned failure!! check status code"
    echo "Status Code: $IGW_RENAME "
    exit $IGW_RENAME_STATUS
fi

#--------------------------------------------------------------------------------------------------
#Step4: Attach the Internet Gateway to the created VPC.
#--------------------------------------------------------------------------------------------------
echo -e "\n"
echo "-----------------------------------------------------------------------------------------------"
echo "Step4: Attach the Internet Gateway to the created VPC."
echo "-----------------------------------------------------------------------------------------------"
IGW_ATTACH=$(aws ec2 attach-internet-gateway \
  --vpc-id $VPC_ID \
  --internet-gateway-id $IGW_ID \
  --region $REGION 2>&1)
IGW_ATTACH_STATUS=$?
if [ $IGW_ATTACH_STATUS -eq 0 ]; then
  echo "  Internet Gateway ID '$IGW_ID' ATTACHED to VPC ID '$VPC_ID'."
else
    echo "##########Error:Gateway not attached to VPC: $?"
    echo " $IGW_ATTACH "
    exit $IGW_ATTACH_STATUS
fi

#--------------------------------------------------------------------------------------------------
#Step5: Create a public Route Table. Attach all subnets created above to the route table.
#--------------------------------------------------------------------------------------------------
# Create Public Route Table
echo -e "\n"
echo "------------------------------------------------------------------------------------------------------------------------------"
echo "Step5: Create a public Route Table. Attach all subnets created above to the route table."
echo "------------------------------------------------------------------------------------------------------------------------------"
PUBLIC_ROUTE_TABLE_ID=$(aws ec2 create-route-table \
  --vpc-id $VPC_ID \
  --query 'RouteTable.{RouteTableId:RouteTableId}' \
  --output text \
  --region $REGION 2>&1)
  ROUTE_TABLE_CREATE_STATUS=$?
if [ $ROUTE_TABLE_CREATE_STATUS -eq 0 ]; then
  echo "Route Table ID '$PUBLIC_ROUTE_TABLE_ID' CREATED."
else
    echo "Error:Route table not created!!"
    echo "$PUBLIC_ROUTE_TABLE_ID "
    exit $ROUTE_TABLE_CREATE_STATUS
fi

# Rename to Public Route Table
echo -e "\n"
echo "***RENAME ROUTE TABLE***"
ROUTE_TABLE_RENAME=$(aws ec2 create-tags \
  --resources $PUBLIC_ROUTE_TABLE_ID \
  --tags "Key=Name,Value=$publicRouteTableName" 2>&1)
ROUTE_TABLE_RENAME_STATUS=$?
if [ $ROUTE_TABLE_RENAME_STATUS -eq 0 ]; then
  echo "Route table ID '$PUBLIC_ROUTE_TABLE_ID' NAMED as '$publicRouteTableName'."
else
    echo "##########Error:ROUTE_TABLE name not added!!"
    echo " $ROUTE_TABLE_RENAME "
    exit $ROUTE_TABLE_RENAME_STATUS
fi

#Create route to Internet Gateway
echo -e "\n"
echo "***CREATE ROUTE TO IGW***"
RESULT=$(aws ec2 create-route \
  --route-table-id $PUBLIC_ROUTE_TABLE_ID \
  --destination-cidr-block 0.0.0.0/0 \
  --gateway-id $IGW_ID \
  --region $REGION)
RESULT_STATUS=$?
if [ $RESULT_STATUS -eq 0 ]; then
  echo "Route to '0.0.0.0/0' via Internet Gateway ID '$IGW_ID' ADDED to Route Table ID '$PUBLIC_ROUTE_TABLE_ID'."
else
    echo "##########Error:Route to Internet gateway not created!!"
    echo " $RESULT "
    exit $RESULT_STATUS
fi


#6.Create a public route in the public route table created above with destination CIDR block 0.0.0.0/0 and internet gateway creted above as the target.

#Associate a Route Table with a Public subnet
echo -e "\n"
echo "***BEGIN SUBNET TO ROUTE TABLE ASSOCIATION***"
Public_Associate_1=$(aws ec2 associate-route-table \
  --subnet-id $PUBLIC_SUBNET_1 \
  --route-table-id $PUBLIC_ROUTE_TABLE_ID \
  --region $REGION)
echo "Public Subnet ID '$PUBLIC_SUBNET_1' ASSOCIATED with Route Table ID" \
  "'$PUBLIC_ROUTE_TABLE_ID'."

echo -e "\n"
Public_Associate_2=$(aws ec2 associate-route-table  \
  --subnet-id $PUBLIC_SUBNET_2 \
  --route-table-id $PUBLIC_ROUTE_TABLE_ID \
  --region $REGION)
echo "Public Subnet ID '$PUBLIC_SUBNET_2' ASSOCIATED with Route Table ID" \
  "'$PUBLIC_ROUTE_TABLE_ID'."

echo -e "\n"
Public_Associate_3=$(aws ec2 associate-route-table  \
  --subnet-id $PUBLIC_SUBNET_3 \
  --route-table-id $PUBLIC_ROUTE_TABLE_ID \
  --region $REGION)
echo "Public Subnet ID '$PUBLIC_SUBNET_3' ASSOCIATED with Route Table ID" \
  "'$PUBLIC_ROUTE_TABLE_ID'."


#---------------------------------------------------------------------------------------------------------------------------------------------------------
#step7.Modify the default security group for your VPC to remove existing rules and add new rules to only allow TCP traffic on port 22 and 80 from anywhere.
#----------------------------------------------------------------------------------------------------------------------------------------------------------
echo -e "\n"

echo "------------------------------------------------------------------------------------------------------------------------------------------------------"
echo "step7: Modify the default security group for your VPC to remove existing rules and add new rules to only allow TCP traffic on port 22 and 80 from anywhere."
echo "----------------------------------------------------------------------------------------------------------------------------------------------------------"
SecGrpJson=($(aws ec2 create-security-group --group-name $security_group --description "My security group" --vpc-id $VPC_ID | grep -oP '[,\s"]+\K.*?(?=")'))
#printf "%s\n" ${SecGrpJson[2]}
SecGrpId=${SecGrpJson[2]}
#echo $SecGrpId

SECURITY_GROUP_RESULT_STATUS=$?
if [ $SECURITY_GROUP_RESULT_STATUS -eq 0 ]; then
  echo "Security Group created with id: $SecGrpId ."
else
    echo "##########Error:Security group not created!!"
    echo " $SecGrpJson"
    exit $SECURITY_GROUP_RESULT_STATUS
fi


# Retrieving CSG
echo -e "\n"
echo "RETRIEVE SECURITY GROUP ID"
SGId=$(aws ec2 describe-security-groups \
	--query 'SecurityGroups[*].{GroupId:GroupId}' \
	--filters "Name=vpc-id,Values=$VPC_ID" "Name=group-name, Values=default" \
	--output text \
 	--region $REGION)
echo $SGId

echo -e "\n"
Port_revoke=$(aws ec2 revoke-security-group-ingress --group-id $SGId --source-group $SGId --protocol all)
Port_TCP_ingress=$(aws ec2 authorize-security-group-ingress --group-id $SGId --protocol tcp --port 22 --cidr $default_ip)
Port_TCP_ingress_Result_Status=$?
Port_HTTP_ingress=$(aws ec2 authorize-security-group-ingress --group-id $SGId --protocol tcp --port 80 --cidr $default_ip)
Port_HTTP_ingress_Result_Status=$?
Port_TCP_egress=$(aws ec2 authorize-security-group-egress --group-id $SGId --protocol tcp --port 22 --cidr $default_ip)
Port_HTTP_egress=$(aws ec2 authorize-security-group-egress --group-id $SGId --protocol tcp --port 80 --cidr $default_ip)
Port_revoke_egress=$(aws ec2 revoke-security-group-egress --group-id $SGId --protocol all --cidr $default_ip)

if [ $Port_TCP_ingress_Result_Status -eq 0 ]; then
  echo "Allowing traffic from port 22 in Security GroupId: $SGId ."

  if [ $Port_HTTP_ingress_Result_Status -eq 0 ]; then
    echo "Allowing traffic from port 80 in Security GroupId: $SGId ."
  else
    echo "##########Error:Modification of exiting rules failed!!"
    echo "Port_HTTP_ingress : ($Port_HTTP_ingress)"
    exit $Port_HTTP_ingress_Result_Status
  fi

else
    echo "##########Error:Modification of exiting rules failed!!"
    echo "Port_TCP_ingress : ($Port_TCP_ingress)"
    exit $Port_TCP_ingress_Result_Status
fi
