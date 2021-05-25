<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title></title>
</head>
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

    <#list hr_contacts?keys as key> 
    <p>${key} : ${hr_contacts[key]} </p>
	</#list> 
</body>
</html>
