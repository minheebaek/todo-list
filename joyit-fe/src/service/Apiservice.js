import { API_BASE_URL } from "../app-config";

export function call(api, method, request){

	// Header
    let headers = new Headers({
        "Content-Type" : "application/json"
    });
    
    // LocalStorage 가져오는 부분
    const accessToken = localStorage.getItem("ACCESS_TOKEN");
    if(accessToken){
        headers.append("Authorization", "Bearer "+accessToken);
    }

	// Options
    let options = {
        headers : headers,
        url : API_BASE_URL + api,
        method : method,
    };

    if(request){
        options.body = JSON.stringify(request);
    }

    return fetch(options.url, options)
        .then(response => 
            response.json().then((json) => {
                if(!response.ok){
                    return Promise.reject(json);
                }
                return json;
            })
        )
        .catch((error) => {
            // 리디렉션
            console.log("error occured!");
            console.log(error);
            console.log(error.status);
            window.location.href = "/login";
            return Promise.reject(error);
            // if(error.status === 403){
                
            // }
                
        });
}

export function signin(userDTO){
    return call("/auth/signin", "POST", userDTO)
        .then((response)=>{
            if(response.token){
                // Token이 존재하는 경우 LocalStorage에 TOKEN 저장
                localStorage.setItem("ACCESS_TOKEN", response.token);
                // 유효한 토큰 값을 가진 경우 메인 페이지로 이동
                window.location.href = "/";
            }
        });
}



export function signout(){
    localStorage.setItem("ACCESS_TOKEN",null);
    window.location.href = "/login";
}

export function signup(userDTO){
    return call("/auth/signup", "POST", userDTO);
}