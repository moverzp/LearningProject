#!/usr/bin/env python
# -*- encoding: utf-8 -*-
# Created on 2019-06-27 00:30:49
# Project: zhihu

from pyspider.libs.base_handler import *
import pymysql
import random


class Handler(BaseHandler):
    crawl_config = {
        'headers': {'User-Agent':'Mozilla/5.0(Macintosh;Intel Mac OS X 10_11_4) AppleWebKit/537.36(KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36',
        'Host':'www.zhihu.com',
         }
    }
    
    def __init__(self):
        self.db = pymysql.connect("localhost", "root", "123458mysql", "wenda")
        
    def add_question(self, title, content, comment_count):
        try:
            cursor = self.db.cursor()
            sql = 'insert into question(title, content, user_id, created_date, comment_count) \
                values("%s", "%s", %d, now(), %d)' % (title, content, random.randint(1, 10), comment_count)
            print(sql)
            cursor.execute(sql)
            qid = cursor.lastrowid
            self.db.commit()
            return qid
        except Exception as e:
            print('Exception: add question', e)
            self.db.rollback()
    
    def add_comment(self, qid, content):
        try:
            cursor = self.db.cursor()
            sql = 'insert into comment(content, entity_type, entity_id, user_id, created_date) \
                values("%s", 1, %d, %d, now())' %(content, qid, random.randint(1, 10))
            print(sql)
            cursor.execute(sql)
            self.db.commit()
        except Exception as e:
            print('Exception: add comment', e)
            self.db.rollback()

    @every(minutes=24 * 60)
    def on_start(self):
        print('爬虫启动！')
        self.crawl('https://www.zhihu.com/topic/19559450/top-answers', callback=self.index_page, validate_cert=False)

    @config(age=10 * 24 * 60 * 60)
    def index_page(self, response):
        print('话题精品页...')
        for each in response.doc('.ContentItem-title > div > a').items():
            href = '/'.join(each.attr.href.split('/')[:-2])
            print(href)
            self.crawl(href, callback=self.detail_page)

    @config(priority=2)
    def detail_page(self, response):
        print('问题明细页...')
        title = response.doc('h1.QuestionHeader-title').text()
        content = response.doc('span.RichText.ztext').html().replace('"', '\\"')
        items = response.doc('div.RichContent-inner').items()
        qid = self.add_question(title, content, sum(1 for x in items))
        
        for i, item in enumerate(response.doc('div.RichContent-inner').items()):
            self.add_comment(qid, item.html().replace('"', '\\"'))
                             
        return {
            'url': response.url,
            'title': response.doc('title').text(),
        }
