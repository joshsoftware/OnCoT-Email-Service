<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title></title>
	<style>
    .center {
  margin: auto;
  width: 55%;
  padding: 20px;
}
  </style>
</head>
<<<<<<< HEAD
<body style="font-family: Arial, Helvetica, sans-serif; color: #46535e; font-size: 14px;">
=======
<body>
<p> Hi Candidate</p>
<p>
  You have been invited to take coding round by <b>${organization}</b>.
  <p>${organization}</p>
    <p>${drive_name}</p>
      <p>${start_time?date} - ${start_time?time}</p>
        <p>${end_time?date} - ${end_time?time}</p>


</p>
</br>
>>>>>>> origin/Daily-Limit-Enhancement-v6

  <div class="center" style="background-color:#f0f0f0;height: auto;padding: 5px;"> 
      <div style="margin: 30px; padding: 10px; background-color:#fff">
        <p> Hi Candidate</p>
        <p>
          You have been invited to take coding round by <b>Josh Software Pvt Ltd</b>.
        </p>
        </br>
        
        <b>Test Details</b>
        <ul>
          <li>
            <b>Test Link:</b> <a href="${url}/${token}">Click here</a>
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
        In case of any queries, contact
        </p>
        <p>
        Neha Vyas- +91 8552026633,
        </p>
        <p>
        Sneha Mantri -   +91 77740 56300
        </p>
        
        <p>
          If you face any issue while online code submission then submit your code <a href="https://forms.gle/hth8Qiif45PukRms7">Here</a>
        </p>
        
        <p>
        Regards,
        <br>
        Team Josh Software Pune.
        </p>        
      </div>    
  </div>


<<<<<<< HEAD
=======
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

>>>>>>> origin/Daily-Limit-Enhancement-v6
</body>
</html>