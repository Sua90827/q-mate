"use client";
import {
  CalendarMinus2,
  House,
  MessageSquareText,
  Settings,
} from "lucide-react";
import Link from "next/link";
import React, { useState } from "react";

export default function Nav() {
  const [active, setActive] = useState("home");
  return (
    <>
      <nav className="w-full h-[70px] bg-secondary flex justify-center items-center sm:hidden ">
        <ul className="w-[320px] gap-14 flex sm:hidden">
          <li>
            <Link href={"/main"}>
              <House
                aria-label="홈"
                size={48}
                onClick={() => setActive("home")}
                className={
                  active === "home" ? "text-primary " : "hover:opacity-70"
                }
              />
            </Link>
          </li>
          <li>
            <Link href={"/record"}>
              <MessageSquareText
                aria-label="우리의 기록"
                size={48}
                onClick={() => setActive("record")}
                className={
                  active === "record" ? "text-primary " : "hover:opacity-70"
                }
              />
            </Link>
          </li>
          <li>
            <Link href={"/schedule"}>
              <CalendarMinus2
                aria-label="일정"
                size={48}
                onClick={() => setActive("schedule")}
                className={
                  active === "schedule" ? "text-primary " : "hover:opacity-70"
                }
              />
            </Link>
          </li>
          <li>
            <Link href={"/settings"}>
              <Settings
                aria-label="설정"
                size={48}
                onClick={() => setActive("settings")}
                className={
                  active === "settings" ? "text-primary " : "hover:opacity-70"
                }
              />
            </Link>
          </li>
        </ul>
      </nav>
      <nav className="w-full h-[70px] sm:flex sm:justify-end  items-center hidden absolute ">
        <ul className="w-[400px] gap-14 hidden sm:flex ">
          <li>
            <Link
              href={"/main"}
              onClick={() => setActive("home")}
              className={
                active === "home"
                  ? "font-bold text-lg  bg-primary rounded-xl py-2 px-3 text-secondary "
                  : "font-bold text-lg hover:opacity-70"
              }
            >
              홈
            </Link>
          </li>
          <li>
            <Link
              href={"/record"}
              onClick={() => setActive("record")}
              className={
                active === "record"
                  ? "font-bold text-lg bg-primary rounded-xl py-2 px-3 text-secondary "
                  : "font-bold text-lg hover:opacity-70"
              }
            >
              우리의 기록
            </Link>
          </li>
          <li>
            <Link
              href={"/schedule"}
              onClick={() => setActive("schedule")}
              className={
                active === "schedule"
                  ? "font-bold text-lg  bg-primary rounded-xl py-2 px-3 text-secondary "
                  : "font-bold text-lg hover:opacity-70"
              }
            >
              캘린더
            </Link>
          </li>
          <li>
            <Link
              href={"/settings"}
              onClick={() => setActive("settings")}
              className={
                active === "settings"
                  ? "font-bold text-lg bg-primary rounded-xl py-2 px-3 text-secondary "
                  : "font-bold text-lg hover:opacity-70"
              }
            >
              설정
            </Link>
          </li>
        </ul>
      </nav>
    </>
  );
}
