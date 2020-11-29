<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE html>
<html lang="en">
<head>
	<title>Table V04</title>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
<!--===============================================================================================-->	
	<link rel="icon" type="image/png" href="images/icons/favicon.ico"/>
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="vendor/bootstrap/css/bootstrap.min.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="fonts/font-awesome-4.7.0/css/font-awesome.min.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="vendor/animate/animate.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="vendor/select2/select2.min.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="vendor/perfect-scrollbar/perfect-scrollbar.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="css/util.css">
	<link rel="stylesheet" type="text/css" href="css/main.css">
	<link rel="stylesheet" type="text/css" href="cssmenu.css">
<!--===============================================================================================-->
</head>
<body>

<div id='cssmenu'>
<ul>
	<li><a href='javascript:document.getGames.submit()'><span>Obtener datos</span></a> </li>
</ul>
</div>

<form class="example" method="post" action="search_game">
	<div class="row">
		<div class="col-md-4">
			<input type="text" placeholder="Search.." name="search">
		</div>
		<div class="col-md-2">
			<button type="submit"><i class="fa fa-search"></i></button>
		</div>
	</div>

    <div class="row">
        <div class="col-md-4 form-inline">
            <input type="radio" id="name" name="searchSelector" value="name">Nombre<br>

        </div>

        <div class="col-md-5 form-inline">
            <input type="radio" id="cassette" name="searchSelector" value="cassette">Cinta<br>
        </div>
    </div>


</form>

	<div class="limiter">
		<div class="container-table100">
			<div class="wrap-table100">
				<div class="table100 ver1 m-b-110">
					<div class="table100-head">
						<table>
							<thead>
								<tr class="row100 head">
									<th class="cell100 column1">Nº</th>
									<th class="cell100 column2">Nombre</th>
									<th class="cell100 column3">Tipo</th>
									<th class="cell100 column4">Cinta</th>
									<th class="cell100 column5">Registro</th>
								</tr>
							</thead>
						</table>
					</div>

					<div class="table100-body js-pscroll">
						<table>
							<tbody>

							<c:forEach var="game" items="${games}">
								<tr class="row100 body">
									<td class="cell100 column1">${game.id}</td>
									<td class="cell100 column2">${game.name}</td>
									<td class="cell100 column3">${game.type}</td>
									<td class="cell100 column4">${game.cassette}</td>
									<td class="cell100 column5">${game.register}</td>
								</tr>
							</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
		</div>
	</div>




<!--===============================================================================================-->
	<script src="vendor/jquery/jquery-3.2.1.min.js"></script>
<!--===============================================================================================-->
	<script src="vendor/bootstrap/js/popper.js"></script>
	<script src="vendor/bootstrap/js/bootstrap.min.js"></script>
<!--===============================================================================================-->
	<script src="vendor/select2/select2.min.js"></script>
<!--===============================================================================================-->
	<script src="vendor/perfect-scrollbar/perfect-scrollbar.min.js"></script>
	<script>
		$('.js-pscroll').each(function(){
			var ps = new PerfectScrollbar(this);

			$(window).on('resize', function(){
				ps.update();
			})
		});
			
		
	</script>
<!--===============================================================================================-->
	<script src="js/main.js"></script>

</body>
</html>