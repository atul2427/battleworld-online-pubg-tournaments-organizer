<?php
include("include/security.php");
include("include/conn.php");

//$selquery = "select * from tbl_user_master where uname='$user'";
//$selres = mysqli_query($conn,$selquery);
//$selres1 = mysqli_fetch_array($selres);
//$full_name = $selres1['fname'] . " " . $selres1['lname'];
//$userid = $selres1['user_id'];
$ch = curl_init();
curl_setopt_array($ch, array(
    CURLOPT_URL => "https://api.envato.com/v3/market/author/sale?code={$code}",
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_TIMEOUT => 20,
    
    CURLOPT_HTTPHEADER => array(
        "Authorization: Bearer {$personalToken}",
        "User-Agent: {$userAgent}"
    )
));

$response = @curl_exec($ch);

$body = @json_decode($response);

if (isset($body->item->name)) {

    $id = $body->item->id;
    $name = $body->item->name;

    if($id == 23898180) {
if(isset($_GET['matchId']))
{
  $matchId = $_GET['matchId'];
  
  $getquery1 = "select id,title from match_details where id={$matchId}";
  $getresult41 = mysqli_query($conn,$getquery1);



if(isset($_POST['btnSave']))
{

  $txtRoomId = mysqli_real_escape_string($conn,$_POST['txtRoomId']);
  $txtRoomPass = mysqli_real_escape_string($conn,$_POST['txtRoomPass']);
  $txtRstatus = mysqli_real_escape_string($conn,$_POST['txtRstatus']);
  $txtRsize = mysqli_real_escape_string($conn,$_POST['txtRsize']);
  
  $txtDate = date("Y-m-d H:i:s");
      
      $insquery = "insert into room_details (id,match_id,room_id,room_pass,room_status,room_size,created) values(null,'$matchId','{$txtRoomId}','{$txtRoomPass}','{$txtRstatus}',{$txtRsize},'{$txtDate}')";
  
    if(mysqli_query($conn,$insquery))
    {
      header("Location:match-list.php");
    }
    else
    {
        //echo $insquery;
        echo '<script type="text/javascript">';
        echo 'setTimeout(function () { swal(
                                              "Oops...",
                                              "Something went wrong !!",
                                              "error"
                                            );';
        echo '}, 1000);</script>';
    }
  


}

}

if(isset($_GET['matchId']))
{
  $matchId = $_GET['matchId'];
  
  $getquery1 = "select * from match_details where id={$matchId}";
  $getresult1 = mysqli_query($conn,$getquery1);
  
}

if(isset($_POST['btnUpdate']))
{

  $txtMtitle = mysqli_real_escape_string($conn,$_POST['txtMtitle']);
  $txtMtime = mysqli_real_escape_string($conn,$_POST['txtMtime']);
  $txtMtype = mysqli_real_escape_string($conn,$_POST['txtMtype']);
  $txtVersion = mysqli_real_escape_string($conn,$_POST['txtVersion']);
  $txtEfees = mysqli_real_escape_string($conn,$_POST['txtEfees']);
  $txtKpoints = mysqli_real_escape_string($conn,$_POST['txtKpoints']);
  $txtWprize = mysqli_real_escape_string($conn,$_POST['txtWprize']);
  $txtEtype = mysqli_real_escape_string($conn,$_POST['txtEtype']);
  $txtMap = mysqli_real_escape_string($conn,$_POST['txtMap']);
  $txtSponsoredBy = mysqli_real_escape_string($conn,$_POST['txtSponsoredBy']);
  $txtSurl = mysqli_real_escape_string($conn,$_POST['txtSurl']);
  $txtMdesc = mysqli_real_escape_string($conn,$_POST['txtMdesc']);
  $chk =  mysqli_real_escape_string($conn,$_POST['checkPmatch']);
  
  if($chk=="yes")
  {
    $chk="yes";
  }
  else
  {
    $chk="no";
  }

  $txtDate = date("Y-m-d H:i:s");
  
    if(isset($_FILES['txtBimg']))
    {
      $file1 = $_FILES['txtBimg'];

      //file properties

      $file1_name=$file1['name'];
      $file1_tmp=$file1['tmp_name'];
      $file1_error=$file1['error'];

      //file extension

      $file_ext=explode('.',$file1_name);
      $file_ext = strtolower($file1_name);

      if($file1_error==0)
      {
        $file1_new = uniqid('',true).'.'.$file_ext;
        $file1_destination='upload/'.$file1_new;
        move_uploaded_file($file1_tmp,$file1_destination);
      }

      if(isset($file1_destination))
      {
        $txtBimg=$file1_destination;
        
      }
      else
      {
        $txtBimg="";
      }
    }
    else
    {
      echo "image not load";
    }

    if(isset($_FILES['txtCimg']))
    {
      $file1 = $_FILES['txtCimg'];

      //file properties

      $file1_name=$file1['name'];
      $file1_tmp=$file1['tmp_name'];
      $file1_error=$file1['error'];

      //file extension

      $file_ext=explode('.',$file1_name);
      $file_ext = strtolower($file1_name);

      if($file1_error==0)
      {
        $file1_new = uniqid('',true).'.'.$file_ext;
        $file1_destination='upload/'.$file1_new;
        move_uploaded_file($file1_tmp,$file1_destination);
      }

      if(isset($file1_destination))
      {
        $txtCimg=$file1_destination;
        
      }
      else
      {
        $txtCimg="";
      }
    }
    else
    {
      echo "image not load";
    }

    if (!empty($_FILES['txtBimg']['name']) and !empty($_FILES['txtCimg']['name'])) {
      
      $updateqry = "update match_details set title='{$txtMtitle}',time='{$txtMtime}',imgBanner='{$txtBimg}',winPrize={$txtWprize},imgCover='{$txtCimg}',perKill={$txtKpoints},entryFee={$txtEfees},matchType='{$txtMtype}',version='{$txtVersion}',map='{$txtMap}',isPrivateMatch='{$chk}',entryType='{$txtEtype}',sponsoredBy='{$txtSponsoredBy}',spectateURL='{$txtSurl}',matchNotes='{$txtMdesc}' where id=$matchId";
    }
    else if (!empty($_FILES['txtBimg']['name'])) {
      
      $updateqry = "update match_details set title='{$txtMtitle}',time='{$txtMtime}',imgBanner='{$txtBimg}',winPrize={$txtWprize},perKill={$txtKpoints},entryFee={$txtEfees},matchType='{$txtMtype}',version='{$txtVersion}',map='{$txtMap}',isPrivateMatch='{$chk}',entryType='{$txtEtype}',sponsoredBy='{$txtSponsoredBy}',spectateURL='{$txtSurl}',matchNotes='{$txtMdesc}' where id=$matchId";
    }
    else if (!empty($_FILES['txtCimg']['name'])) {
      
      $updateqry = "update match_details set title='{$txtMtitle}',time='{$txtMtime}',winPrize={$txtWprize},imgCover='{$txtCimg}',perKill={$txtKpoints},entryFee={$txtEfees},matchType='{$txtMtype}',version='{$txtVersion}',map='{$txtMap}',isPrivateMatch='{$chk}',entryType='{$txtEtype}',sponsoredBy='{$txtSponsoredBy}',spectateURL='{$txtSurl}',matchNotes='{$txtMdesc}' where id=$matchId";
    }
    else // no image uploaded
    {
      $updateqry = "update match_details set title='{$txtMtitle}',time='{$txtMtime}',winPrize={$txtWprize},perKill={$txtKpoints},entryFee={$txtEfees},matchType='{$txtMtype}',version='{$txtVersion}',map='{$txtMap}',isPrivateMatch='{$chk}',entryType='{$txtEtype}',sponsoredBy='{$txtSponsoredBy}',spectateURL='{$txtSurl}',matchNotes='{$txtMdesc}' where id=$matchId";
    }

    if(mysqli_query($conn,$updateqry))
    {
      header("Location:match-list.php");
    }
    else
    {
        //echo $updateqry;
        echo '<script type="text/javascript">';
        echo 'setTimeout(function () { swal(
                                              "Oops...",
                                              "Something went wrong !!",
                                              "error"
                                            );';
        echo '}, 1000);</script>';
    }
  
}
} else {
        header("location:error.php");
      exit;
    }
}
else
{
    header("location:error.php");
    exit;
}
?>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="description" content="">
		<meta name="author" content="">

		<title>Room Details</title>

    <?php include_once("include/head-section.php"); ?>

    
	</head>

	<body class="fixed-left">

		<!-- Begin page -->
		<div id="wrapper">

      <!-- topbar and sidebar -->
      <?php include_once("include/navbar.php"); ?>

			<!-- ============================================================== -->
			<!-- Start right Content here -->
			<!-- ============================================================== -->
			<div class="content-page">
				<!-- Start content -->
				<div class="content">
					<div class="container">

            <!-- Page Content -->
            <div class="row">
              <div class="col-lg-12 col-md-12 col-sm-12">
                <div class="card-box">
                  <?php if(isset($_GET['matchId'])&isset($_GET['roomId'])) { ?>

                  <?php 
                  
                  $selres4 = mysqli_num_rows($getresult1);
                  if ($selres4 == 0) {
                
                    //echo"<script>alert(\"You have entered a wrong url\");</script>";
                    
                  ?>

                  <div class="wrapper-page">
                    <div class="ex-page-content text-center">
                      <div class="text-error">
                        <span class="text-primary">4</span><i class="ti-face-sad text-pink"></i><span class="text-info">4</span>
                      </div>
                      <h2>Whoo0ps! Page not found</h2>
                      <br>
                      <p class="text-muted">
                        This page cannot found or is missing.
                      </p>
                      <p class="text-muted">
                        Use the navigation above or the button below to get back and track.
                      </p>
                      <br>
                      <!-- <a class="btn btn-default waves-effect waves-light" href="dashboard.php"> Return Home</a> -->

                    </div>
                  </div>

                  <?php } else { 
                    $getres1 = mysqli_fetch_array($getresult1); 
                  ?>

                  <h4 class="m-t-0 header-title"><b>Edit Match Details</b></h4>
                  <p class="text-muted font-13 m-b-30">
                      Update match details here.
                  </p>
                  <form action="match-detail.php?matchId=<?php echo $_GET['matchId'];?>&roomId=<?php echo $_GET['roomId'];?>" data-parsley-validate novalidate enctype="multipart/form-data" method="post">
                    <div class="row">
                      <div class="col-md-6">
                        <div class="form-group">
                          <label for="txtMtitle">Match Title*</label>
                          <input type="text" parsley-trigger="change" placeholder="Enter match title" value="<?php echo $match_title; ?>" disabled class="form-control">
                        </div>
                      </div>
                    </div>
                    <div class="row">
                      <div class="col-md-6">
                        <div class="form-group">
                          <label for="txtRoomId">Room Id*</label>
                          <input id="txtRoomId" value="<?php echo $getres1['room_id']?>" name="txtRoomId" type="text" required class="form-control">
                        </div>
                      </div>
                      <div class="col-md-6">
                        <div class="form-group">
                          <label for="txtRoomPass">Room Password*</label>
                          <input type="text" value="<?php echo $getres1['room_pass']?>" name="txtRoomPass" parsley-trigger="change" required class="form-control" id="txtRoomPass">
                        </div>
                      </div>
                    </div>
                    <div class="row">
                      <div class="col-md-4">
                        <div class="form-group">
                          <label for="txtRstatus">Room Status*</label>
                          <select name="txtRstatus" class="form-control" id="txtRstatus" required>
                            <option value="">--Select--</option>
                            <option <?php if($getres1['room_status']=='0'){ echo "selected"; } ?> value="0">Hide</option>
                            <option <?php if($getres1['room_status']=='1'){ echo "selected"; } ?> value="1">Show</option>
                          </select>
                        </div>
                      </div>
                      <div class="col-md-4">
                        <div class="form-group">
                          <label for="txtRsize">Room Size*</label>
                          <input type="number" value="<?php echo $getres1['room_size']?>" name="txtRsize" parsley-trigger="change" required class="form-control" max="100" min="10" id="txtRsize">
                        </div>
                      </div>
                    </div>
                    <br>
                    <div class="row">
                      <div class="col-md-12">
                        <div class="form-group text-right m-b-0">
                          <button class="btn btn-primary waves-effect waves-light" type="submit" name="btnUpdate" id="btnUpdate" > Update</button>
                          <!-- <a href="user-list.php" class="btn btn-default waves-effect waves-light m-l-5"> Cancel</a> -->
                          <a href="javascript:void(0);" class="btn btn-default waves-effect waves-light" onclick="history.back();"> Cancel</a>
                        </div>
                      </div>

                    </div>
                  </form>

                  <?php } ?> <!-- else part completed here (retrieving details) -->

                  <?php } else if(isset($_GET['matchId']))  
                  { 
                    $getres14 = mysqli_fetch_array($getresult41);  
                    $match_id = $getres14['id'];
                    $match_title = $getres14['title'];

                  ?>

                  <h4 class="m-t-0 header-title"><b>Add Room Details</b></h4>
                  <p class="text-muted font-13 m-b-30">
                      Fill all necessary data to create Room.
                  </p>
                  <form action="room-detail.php?matchId=<?php echo $_GET['matchId'];?>" data-parsley-validate novalidate method="post">
                    <div class="row">
                      <div class="col-md-6">
                        <div class="form-group">
                          <label for="txtMtitle">Match Title*</label>
                          <input type="text" parsley-trigger="change" placeholder="Enter match title" value="<?php echo $match_title; ?>" disabled class="form-control">
                        </div>
                      </div>
                    </div>
                    <div class="row">
                      <div class="col-md-6">
                        <div class="form-group">
                          <label for="txtRoomId">Room Id*</label>
                          <input id="txtRoomId" name="txtRoomId" type="text" required class="form-control">
                        </div>
                      </div>
                      <div class="col-md-6">
                        <div class="form-group">
                          <label for="txtRoomPass">Room Password*</label>
                          <input type="text" name="txtRoomPass" parsley-trigger="change" required class="form-control" id="txtRoomPass">
                        </div>
                      </div>
                    </div>
                    <div class="row">
                      <div class="col-md-4">
                        <div class="form-group">
                          <label for="txtRstatus">Room Status*</label>
                          <select name="txtRstatus" class="form-control" id="txtRstatus" required>
                            <option value="">--Select--</option>
                            <option value="0">Hide</option>
                            <option value="1">Show</option>
                          </select>
                        </div>
                      </div>
                      <div class="col-md-4">
                        <div class="form-group">
                          <label for="txtRsize">Room Size*</label>
                          <input type="number" name="txtRsize" parsley-trigger="change" required class="form-control" max="100" min="10" id="txtRsize">
                        </div>
                      </div>
                    </div>
                    <br>
                    <div class="row">
                      <div class="col-md-12">
                        <div class="form-group text-right m-b-0">
                          <button class="btn btn-primary waves-effect waves-light" type="submit" name="btnSave" id="btnSave" > Save</button>
                          <!-- <a href="user-list.php" class="btn btn-default waves-effect waves-light m-l-5"> Cancel</a> -->
                          <a href="javascript:void(0);" class="btn btn-default waves-effect waves-light" onclick="history.back();"> Cancel</a>
                        </div>
                      </div>

                    </div>
                  </form>

                  <?php } else { ?>
                    <div class="wrapper-page">
                      <div class="ex-page-content text-center">
                        <div class="text-error">
                          <span class="text-primary">4</span><i class="ti-face-sad text-pink"></i><span class="text-info">4</span>
                        </div>
                        <h2>Whoo0ps! Page not found</h2>
                        <br>
                        <p class="text-muted">
                          This page cannot found or is missing.
                        </p>
                        <p class="text-muted">
                          Use the navigation above or the button below to get back and track.
                        </p>
                        <br>
                        <!-- <a class="btn btn-default waves-effect waves-light" href="dashboard.php"> Return Home</a> -->

                      </div>
                    </div>
                  <?php } ?>

                </div>
              </div>
            </div>
            <!-- /Page Content -->

          </div> <!-- container -->
                               
        </div> <!-- content -->

        <?php include_once("include/footer.php"); ?>

      </div>
      <!-- ============================================================== -->
      <!-- End Right content here -->
      <!-- ============================================================== -->

    </div>
    <!-- END wrapper -->

    <script>
        var resizefunc = [];
    </script>

    <!-- jQuery  -->
    <?php include_once("include/common_js.php"); ?>

    <script src="assets/js/jquery.core.js"></script>
    <script src="assets/js/jquery.app.js"></script>
	  <script type="text/javascript" src="assets/plugins/parsleyjs/parsley.min.js"></script>
     	
  </body>
</html>