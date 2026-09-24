import {
  Host
  ,customer
} from "../../providers/constants";
import {
  httpService
} from "../../providers/httpService";
function loadUser(id = "") {
  return httpService.get(customer + "/" + id);
}

export const userService = {
  loadUser:loadUser
}