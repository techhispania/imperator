document.addEventListener("DOMContentLoaded", () => {
	setInterval(() => {
		console.log("Checking services readiness");
		
		// TODO get all services from html page
		const services = document.querySelectorAll(".service-data");
		
		console.log(`Found ${services.length} services`)
		
		services.forEach(async service => {
			
			var id = service.id
			var service_name = id.split("service_")[1]
			var content = service.textContent
			
			console.log(`service ${service_name}: ${content}`)
			
			service_status = await checkService(service_name)
			
			var text = document.querySelector(`#service_${service_name}`)
			text.textContent = `Port: ${service_status.body.port} | PID: ${service_status.body.pid}`
			
			var circle_class = "bg-danger"
			if ("RUNNING" === service_status.body.status) {
				circle_class = "bg-success"
			}
			
			var circle = document.querySelector(`#circle_${service_name}`)
			circle.classList.remove("bg_danger")
			circle.classList.remove("bg-success")
			circle.classList.add(circle_class)
		})
	}, 5000);
	
	async function checkService(service_name) {
		try {
			const response = await fetch(`http://localhost:8080/api/service/check`, {
					method: "POST",
					headers: {
						"Content-Type": "application/json",
						"Accept": "application/json"
					},
					body: JSON.stringify({
						serviceName: service_name,
					})
				})
				
				
			if (!response.ok) {
				throw new Error(`HTTP error! Status: ${response.status}`)
			}
			
			const data = await response.json()
			console.log(`Name: ${data.body.serviceName}, Port: ${data.body.port}, PID: ${data.body.pid}, Status: ${data.body.status}`)
			
			return data
		} catch (err) {
			console.error("Error checking service " + service_name, err)
		}
	}
});