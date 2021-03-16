import { DELETE_PROJECT, GET_PROJECT, GET_PROJECTS } from "../actions/types";

const initialState = {};

export default function project(state = initialState, action) {
  switch (action.type) {
    case GET_PROJECTS:
      return {
        ...state,
        projects: action.payload,
      };

    case GET_PROJECT:
      return {
        ...state,
        project: action.payload,
      };
    case DELETE_PROJECT:
      return {
        ...state,
        projects: state.projects.filter(
          (p) => p.projectIdentifier !== action.payload
        ),
      };

    default:
      return state;
  }
}
