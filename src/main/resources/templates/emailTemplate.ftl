<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title></title>
	<style>
    .center {
  margin: auto;
  width: 55%;
  padding: 20px;
  resize:both;
  overflow:auto;
}
@media (min-width: 360px){
	body{
		font-size: 10px;
	}
	.center{
		width: 90%;
  		padding: 10px;
  		resize:both;
  		overflow:auto;
	}
}
  </style>
  <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body style="font-family: Arial, Helvetica, sans-serif; color: #46535e; font-size: 14px;">

  <div class="center" style="background-color:#f0f0f0;height: auto;padding: 5px;"> 
      <div style="margin: 30px; padding: 10px; background-color:#fff">
        <p> Hi Candidate,</p>
<p>
  You have been invited to take coding round by <b>${organization}</b>.
</p>
</br>       
        <b>Test Details</b>
        <ul>
          <li>
            <b>Test Link:</b> <a href="${url}/${token}">Click here</a>
          </li>
          <li>
            <b>Test Start Time:</b> ${start_time?date} - ${start_time?time}
          </li>
        </ul>
        
        <b>Important</b>
        <ul>
          <li>Please use Chrome or Firefox for taking the test</li>
          <li>You can select any language from the dropdown provided on editor</li>
        </ul>
        
        </br>
        </br>
        <p>
        </br>
</br>
<p>
In case of any queries, contact
</p>
<p>
    <#list hr_contacts?keys as key> 
    <p>${key} : ${hr_contacts[key]} </p>
	</#list> 
</p>


<p>
  If you face any issue while online code submission then submit your code <a href="${googleFormUrl}">Here</a>
</p>

<p>
Regards,
<br>
Team ${organization}.
</p>
        
      </div>    
  </div>

</body>
</html>